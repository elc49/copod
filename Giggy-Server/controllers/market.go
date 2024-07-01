package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type MarketController struct {
	r *repositories.MarketRepository
}

func (c *MarketController) Init(queries *db.Queries) {
	c.r = &repositories.MarketRepository{}
	c.r.Init(queries)
}

func (c *MarketController) GetMarketsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Market, error) {
	return c.r.GetMarketsBelongingToFarm(ctx, id)
}

func (c *MarketController) GetMarketByID(ctx context.Context, id uuid.UUID) (*model.Market, error) {
	return c.r.GetMarketByID(ctx, id)
}

func (c *MarketController) CreateFarmMarket(ctx context.Context, args db.CreateFarmMarketParams) (*model.Market, error) {
	return c.r.CreateFarmMarket(ctx, args)
}

func (c *MarketController) GetNearbyMarkets(ctx context.Context, args db.GetNearbyMarketsParams) ([]*model.Market, error) {
	return c.r.GetNearbyMarkets(ctx, args)
}
