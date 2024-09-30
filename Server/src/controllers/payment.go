package controllers

import (
	"context"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/repositories"
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

func (c *PaymentController) BuyRights(ctx context.Context, args db.BuyRightsParams) (*model.Payment, error) {
	return c.r.BuyRights(ctx, args)
}
