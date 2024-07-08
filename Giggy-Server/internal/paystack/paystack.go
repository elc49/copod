package paystack

import (
	"bytes"
	"context"
	"encoding/json"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/cache"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/redis/go-redis/v9"
	"github.com/sirupsen/logrus"
)

var PayStack *paystack

type paystack struct {
	config config.Paystack
	log    *logrus.Logger
	pubsub *redis.Client
}

type Paystack interface {
	ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error)
	ReconcileMpesaChargeCallback(ctx context.Context, input model.ChargeMpesaPhoneCallbackRes) error
}

func New() {
	PayStack = &paystack{
		config.Configuration.Paystack,
		logger.GetLogger(),
		cache.GetCache().GetRedis(),
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

	return chargeRes, nil
}

func (p *paystack) ReconcileMpesaChargeCallback(ctx context.Context, input model.ChargeMpesaPhoneCallbackRes) error {
	go func() {
		u := model.PaymentUpdate{
			ReferenceID: input.Data.Reference,
			Status:      input.Data.Status,
		}
		update, err := json.Marshal(u)
		if err != nil {
			p.log.WithError(err).Error("paystack: json.Marshal update")
			return
		}
		pubSubErr := p.pubsub.Publish(context.Background(), cache.PAYMENT_UPDATES, update).Err()
		if pubSubErr != nil {
			p.log.WithError(pubSubErr).Errorf("paystack: pubsub.Publish update")
			return
		}
	}()

	return nil
}
