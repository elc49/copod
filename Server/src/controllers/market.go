package controllers

import (
	"context"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/repositories"
	"github.com/google/uuid"
)

type MarketController struct {
	r *repositories.MarketRepository
}

func (c *MarketController) Init(store postgres.Store) {
	c.r = &repositories.MarketRepository{}
	c.r.Init(store)
}

func (c *MarketController) GetMarketsBelongingToFarm(ctx context.Context, args db.GetMarketsBelongingToFarmParams) ([]*model.Market, error) {
	return c.r.GetMarketsBelongingToFarm(ctx, args)
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

func (c *MarketController) SetMarketStatus(ctx context.Context, args db.SetMarketStatusParams) (*model.Market, error) {
	return c.r.SetMarketStatus(ctx, args)
}

func (c *MarketController) GetLocalizedMachineryMarkets(ctx context.Context, userID uuid.UUID, args db.GetLocalizedMachineryMarketsParams) ([]*model.Market, error) {
	return c.r.GetLocalizedMachineryMarkets(ctx, userID, args)
}
