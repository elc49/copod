package controllers

import (
	"context"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/elc49/vuno/Server/src/repositories"
	"github.com/google/uuid"
)

type MarketController struct {
	r *repositories.MarketRepository
}

func (c *MarketController) Init(store postgres.Store) {
	c.r = &repositories.MarketRepository{}
	c.r.Init(store)
}

func (c *MarketController) GetMarketsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Market, error) {
	return c.r.GetMarketsBelongingToFarm(ctx, id)
}

func (c *MarketController) GetMarketByID(ctx context.Context, id uuid.UUID) (*model.Market, error) {
	return c.r.GetMarketByID(ctx, id)
}

func (c *MarketController) CreateFarmMarket(ctx context.Context, args db.CreateFarmMarketParams) (*model.Market, error) {
	args.RunningVolume = args.Volume
	return c.r.CreateFarmMarket(ctx, args)
}

func (c *MarketController) GetLocalizedMarkets(ctx context.Context, userID uuid.UUID, args db.GetLocalizedMarketsParams) ([]*model.Market, error) {
	return c.r.GetLocalizedMarkets(ctx, userID, args)
}
