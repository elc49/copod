package paystack

import (
	"bytes"
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"net/http"
	"sync"

	"github.com/elc49/copod/Server/src/cache"
	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/subscription"
	"github.com/redis/go-redis/v9"
	"github.com/sirupsen/logrus"
)

var PayStack *paystack

type paystack struct {
	config config.Paystack
	log    *logrus.Logger
	pubsub *redis.Client
	store  postgres.Store
	mu     sync.Mutex
}

type Paystack interface {
	ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error)
	ReconcilePaystackCallback(ctx context.Context, input model.PaystackWebhook) error
	VerifyTransactionByReferenceID(ctx context.Context, referenceId string) (*model.PaystackVerifyTransaction, error)
}

func New(store postgres.Store) {
	PayStack = &paystack{
		config.Configuration.Paystack,
		logger.GetLogger(),
		cache.GetCache().GetRedis(),
		store,
		sync.Mutex{},
	}
}

func GetPaystackService() Paystack { return PayStack }

func (p *paystack) ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error) {
	var chargeRes *model.ChargeMpesaPhoneRes

	chargeApi := p.config.BaseApi + "/charge"
	input.Provider.Provider = p.config.Provider
	if p.config.Env == "test" {
		input.Provider.Phone = p.config.MobileTestAccount
	}

	payload, err := json.Marshal(input)
	if err != nil {
		p.log.WithError(err).Errorf("paystack: ChargeMpesaPhone: json.Marshal")
		return nil, err
	}

	req, err := http.NewRequest("POST", chargeApi, bytes.NewBuffer(payload))
	if err != nil {
		p.log.WithError(err).Errorf("paystack: http.NewRequest")
		return nil, err
	}
	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer "+p.config.SecretKey)

	c := &http.Client{}
	res, err := c.Do(req)
	if err != nil {
		p.log.WithError(err).Errorf("paystack: htt.Client{}")
		return nil, err
	}

	if err := json.NewDecoder(res.Body).Decode(&chargeRes); err != nil {
		p.log.WithError(err).Errorf("paystack: jwt.NewDecoder")
		return nil, err
	}

	user, err := p.store.StoreReader.GetUserByID(ctx, input.UserID)
	if err != nil {
		p.log.WithError(err).Error("paystack: db.GetUserByID")
		return nil, err
	}

	args := db.BuyRightsParams{
		Customer:    user.Phone,
		Amount:      int32(input.Amount / 100),
		Reason:      input.Reason,
		Currency:    input.Currency,
		Status:      chargeRes.Data.Status,
		UserID:      input.UserID,
		ReferenceID: sql.NullString{String: chargeRes.Data.Reference, Valid: true},
	}
	_, buyErr := p.store.StoreWriter.BuyRights(ctx, args)
	if buyErr != nil {
		p.log.WithError(buyErr).Errorf("paystack: goroutine: db.BuyRights")
		return nil, buyErr
	}

	return chargeRes, nil
}

func (p *paystack) ReconcilePaystackCallback(ctx context.Context, input model.PaystackWebhook) error {
	p.mu.Lock()
	defer p.mu.Unlock()

	payment, err := p.store.StoreReader.GetRightPurchasePaymentByReferenceID(ctx, sql.NullString{String: input.Data.Reference, Valid: true})
	if err != nil && err != sql.ErrNoRows {
		p.log.WithError(err).Error("paystack: GetRightsPurchageByReferenceID")
		return err
	}

	if err != nil {
		p.log.WithError(err).Error("paystack: json.Marshal update")
		return err
	}

	switch payment.Reason {
	case "poster_rights":
		args := db.SetUserPosterRightsParams{
			ID:              payment.UserID,
			HasPosterRights: true,
		}
		p.store.StoreWriter.SetUserPosterRights(ctx, args)
	case "farming_rights":
		args := db.SetUserFarmingRightsParams{
			ID:               payment.UserID,
			HasFarmingRights: true,
		}
		p.store.StoreWriter.SetUserFarmingRights(ctx, args)
	default:
	}

	pubPayload := model.PaystackPaymentUpdate{
		ReferenceID: input.Data.Reference,
		Status:      input.Data.Status,
		SessionID:   payment.UserID,
	}
	pBytes, err := json.Marshal(pubPayload)
	if err != nil {
		p.log.WithError(err).Error("paystack: pubsub: json.Marshal pubPayload")
		return err
	}

	updateArgs := db.UpdatePaystackPaymentStatusParams{
		ReferenceID: sql.NullString{String: input.Data.Reference, Valid: true},
		Status:      input.Data.Status,
	}
	_, uErr := p.store.StoreWriter.UpdatePaystackPaymentStatus(ctx, updateArgs)
	if uErr != nil {
		p.log.WithError(uErr).Error("paystack: UpdatePaystackPaymentStatus")
		return uErr
	}

	pubErr := p.pubsub.Publish(ctx, subscription.PAYMENT_UPDATES, pBytes).Err()
	if pubErr != nil {
		p.log.WithError(pubErr).Error("paystack: pubsub: ReconcileMpesaChargeCallback")
		return pubErr
	}

	return nil
}

func (p *paystack) VerifyTransactionByReferenceID(ctx context.Context, referenceID string) (*model.PaystackVerifyTransaction, error) {
	verifyRes := new(model.PaystackVerifyTransaction)
	verifyUrl := fmt.Sprintf("%s/transaction/verify/%s", p.config.BaseApi, referenceID)

	req, err := http.NewRequest("GET", verifyUrl, nil)
	if err != nil {
		p.log.WithError(err).Errorf("paystack: VerifyTransactionByReferenceID new request")
		return nil, err
	}
	req.Header.Set("Authorization", "Bearer "+p.config.SecretKey)

	c := &http.Client{}
	res, err := c.Do(req)
	if err != nil {
		p.log.WithError(err).Error("paystack: http.Do VerifyTransactionByReferenceID")
		return nil, err
	}

	if marshalErr := json.NewDecoder(res.Body).Decode(&verifyRes); marshalErr != nil {
		p.log.WithError(marshalErr).Error("paystack: json.NewDecoder VerifyTransactionByReferenceID")
		return nil, marshalErr
	}

	return verifyRes, nil
}
