package paystack

import (
	"bytes"
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/cache"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/redis/go-redis/v9"
	"github.com/sirupsen/logrus"
)

var PayStack *paystack

type paystack struct {
	config config.Paystack
	log    *logrus.Logger
	pubsub *redis.Client
	db     *db.Queries
}

type Paystack interface {
	ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error)
	ReconcileMpesaChargeCallback(ctx context.Context, input model.ChargeMpesaPhoneCallbackRes) error
	VerifyTransactionByReferenceID(ctx context.Context, referenceId string) (*model.MpesaTransactionVerification, error)
}

func New(db *db.Queries) {
	PayStack = &paystack{
		config.Configuration.Paystack,
		logger.GetLogger(),
		cache.GetCache().GetRedis(),
		db,
	}
}

func GetPaystackService() Paystack { return PayStack }

func (p *paystack) ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error) {
	var chargeRes *model.ChargeMpesaPhoneRes

	chargeApi := p.config.BaseApi + "/charge"
	input.Provider.Provider = p.config.Provider
	if p.config.Env == "development" {
		input.Provider.Phone = p.config.MobileTestAccount
	}

	payload, err := json.Marshal(input)
	if err != nil {
		p.log.WithError(err).Errorf("paystack: ChargeMpesaPhone: json.Marshal")
		return nil, err
	}

	req, err := http.NewRequest("POST", chargeApi, bytes.NewBuffer(payload))
	if err != nil {
		return nil, err
	}
	req.Header.Add("Content-Type", "application/json")
	req.Header.Add("Authorization", "Bearer "+p.config.SecretKey)

	c := &http.Client{}
	res, err := c.Do(req)
	if err != nil {
		return nil, err
	}

	if err := json.NewDecoder(res.Body).Decode(&chargeRes); err != nil {
		return nil, err
	}

	user, err := p.db.GetUserByID(ctx, input.UserID)
	if err != nil {
		p.log.WithError(err).Error("paystack: db.GetUserByID")
		return nil, err
	}

	args := db.BuyRightsParams{
		Customer:    user.Phone,
		Amount:      int32(input.Amount),
		Reason:      input.Reason,
		Status:      chargeRes.Data.Status,
		UserID:      input.UserID,
		ReferenceID: sql.NullString{String: chargeRes.Data.Reference, Valid: true},
	}
	_, buyErr := p.db.BuyRights(ctx, args)
	if buyErr != nil {
		p.log.WithError(buyErr).Errorf("paystack: goroutine: db.BuyRights")
		return nil, buyErr
	}

	return chargeRes, nil
}

func (p *paystack) ReconcileMpesaChargeCallback(ctx context.Context, input model.ChargeMpesaPhoneCallbackRes) error {
	payment, err := p.db.GetRightPurchasePaymentByReferenceID(ctx, sql.NullString{String: input.Data.Reference, Valid: true})
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
		p.db.SetUserPosterRights(ctx, args)
	case "farming_rights":
		args := db.SetUserFarmingRightsParams{
			ID:               payment.UserID,
			HasFarmingRights: true,
		}
		p.db.SetUserFarmingRights(ctx, args)
	default:
	}
	return nil
}

func (p *paystack) VerifyTransactionByReferenceID(ctx context.Context, referenceID string) (*model.MpesaTransactionVerification, error) {
	verifyRes := new(model.MpesaTransactionVerification)
	verifyUrl := fmt.Sprintf("%s/transaction/verify/%s", p.config.BaseApi, referenceID)

	req, err := http.NewRequest("GET", verifyUrl, nil)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Authorization", "Bearer "+p.config.SecretKey)

	c := &http.Client{}
	res, err := c.Do(req)
	if err != nil {
		return nil, err
	}

	if marshalErr := json.NewDecoder(res.Body).Decode(&verifyRes); marshalErr != nil {
		p.log.WithError(marshalErr).Error("paystack: json VerifyTransactionByReferenceID res")
		return nil, marshalErr
	}

	return verifyRes, nil
}
