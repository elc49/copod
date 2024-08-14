package controllers

import (
	"context"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/repositories"
	"github.com/google/uuid"
)

type PaymentController struct {
	r *repositories.PaymentRepository
}

func (c *PaymentController) Init(store postgres.Store) {
	c.r = &repositories.PaymentRepository{}
	c.r.Init(store)
}

func (c *PaymentController) GetPaymentsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Payment, error) {
	return c.r.GetPaymentsBelongingToFarm(ctx, id)
}
