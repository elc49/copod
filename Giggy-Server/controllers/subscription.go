package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/paystack"
)

type SubscriptionController struct {
	paystack paystack.Paystack
}

func (c *SubscriptionController) Init() {
	c.paystack = paystack.GetPaystackService()
}

func (c *SubscriptionController) ChargeMpesaPhone(ctx context.Context, input model.ChargeMpesaPhoneInput) (*model.ChargeMpesaPhoneRes, error) {
	return c.paystack.ChargeMpesaPhone(ctx, input)
}
