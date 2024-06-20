package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type OrderController struct {
	r *repositories.OrderRepository
}

func (c *OrderController) Init(queries *db.Queries) {
	c.r = &repositories.OrderRepository{}
	c.r.Init(queries)
}

func (c *OrderController) GetOrdersBelongingToStore(ctx context.Context, id uuid.UUID) ([]*model.Order, error) {
	return c.r.GetOrdersBelongingToStore(ctx, id)
}
