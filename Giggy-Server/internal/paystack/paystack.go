package paystack

import (
	"bytes"
	"context"
	"encoding/json"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/sirupsen/logrus"
)

var PayStack *paystack

type paystack struct {
	config  config.Paystack
	queries *db.Queries
	log     *logrus.Logger
}

type Paystack interface {
	ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error)
	ReconcileMpesaChargeCallback(ctx context.Context, input model.ChargeMpesaPhoneCallbackRes) error
}

func New(cfg config.Paystack, queries *db.Queries) {
	PayStack = &paystack{
		cfg,
		queries,
		logger.GetLogger(),
	}
}

func GetPaystackService() Paystack { return PayStack }

func (p *paystack) ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error) {
	var chargeRes *model.ChargeMpesaPhoneRes
	chargeApi := p.config.BaseApi + "/charge"
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
	return nil
}
