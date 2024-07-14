package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type CartController struct {
	r *repositories.CartRepository
}

func (c *CartController) Init(db *db.Queries) {
	c.r = &repositories.CartRepository{}
	c.r.Init(db)
}

func (c *CartController) AddToCart(ctx context.Context, args db.AddToCartParams) (*model.Cart, error) {
	return c.r.AddToCart(ctx, args)
}

func (c *CartController) GetUserCartItems(ctx context.Context, userID uuid.UUID) ([]*model.Cart, error) {
	return c.r.GetUserCartItems(ctx, userID)
}

func (c *CartController) DeleteCartItem(ctx context.Context, cartID uuid.UUID) (bool, error) {
	return c.r.DeleteCartItem(ctx, cartID)
}
