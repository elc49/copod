package paystack

import (
	"bytes"
	"context"
	"database/sql"
	"encoding/json"
	"net/http"
	"time"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/cache"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/jwt"
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

	jwtService := jwt.GetJwtService()
	jwt, err := jwtService.Sign(jwt.NewPayload(payment.UserID.String(), jwtService.GetExpiry()))
	if err != nil {
		p.log.WithError(err).Error("paystack: jwtService.Sign")
		return err
	}
	u := model.PaymentUpdate{
		ReferenceID: input.Data.Reference,
		Status:      input.Data.Status,
		SessionID:   payment.UserID,
		Token:       jwt,
	}
	update, err := json.Marshal(u)
	if err != nil {
		p.log.WithError(err).Error("paystack: json.Marshal update")
		return err
	}

	go func() {
		time.Sleep(2 * time.Second)
		pubSubErr := p.pubsub.Publish(context.Background(), cache.PAYMENT_UPDATES, update).Err()
		if pubSubErr != nil {
			p.log.WithError(pubSubErr).Errorf("paystack: pubsub.Publish update")
			return
		}
	}()

	return nil
}
