package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/util"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
)

type CartController struct {
	r *repositories.CartRepository
}

func (c *CartController) Init(db *db.Queries) {
	c.r = &repositories.CartRepository{}
	c.r.Init(db)
}

func (c *CartController) AddToCart(ctx context.Context, input model.AddToCartInput) (*model.Cart, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	args := db.AddToCartParams{
		Volume:   int32(input.Volume),
		UserID:   userId,
		MarketID: input.MarketID,
		FarmID:   input.FarmID,
	}
	return c.r.AddToCart(ctx, args)
}

func (c *CartController) GetUserCartItems(ctx context.Context) ([]*model.Cart, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	return c.r.GetUserCartItems(ctx, userId)
}
