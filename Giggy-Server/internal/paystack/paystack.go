package paystack

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

var PayStack *paystack

type paystack struct {
	config  config.Paystack
	queries *db.Queries
}

type Paystack interface {
	ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error)
	ReconcileMpesaChargeCallback(ctx context.Context, input model.ChargeMpesaPhoneCallbackRes) error
}

func New(cfg config.Paystack, queries *db.Queries) {
	PayStack = &paystack{
		cfg,
		queries,
	}
}

func GetPaystackService() Paystack { return PayStack }

func (p *paystack) ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error) {
	return nil, nil
}

func (p *paystack) ReconcileMpesaChargeCallback(ctx context.Context, input model.ChargeMpesaPhoneCallbackRes) error {
	return nil
}
