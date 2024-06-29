package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type MarketRepository struct {
	queries *db.Queries
}

func (r *MarketRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *MarketRepository) GetMarketsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Market, error) {
	var markets []*model.Market
	ps, err := r.queries.GetMarketsBelongingToFarm(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range ps {
		market := &model.Market{
			ID:           item.ID,
			Name:         item.Product,
			Image:        item.Image,
			Volume:       int(item.Volume),
			PricePerUnit: int(item.PricePerUnit),
			CreatedAt:    item.CreatedAt,
			UpdatedAt:    item.UpdatedAt,
		}

		markets = append(markets, market)
	}

	return markets, nil
}

func (r *MarketRepository) GetMarketByID(ctx context.Context, id uuid.UUID) (*model.Market, error) {
	p, err := r.queries.GetMarketByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:           p.ID,
		Name:         p.Product,
		Image:        p.Image,
		Volume:       int(p.Volume),
		PricePerUnit: int(p.PricePerUnit),
		CreatedAt:    p.CreatedAt,
		UpdatedAt:    p.UpdatedAt,
	}, nil
}

func (r *MarketRepository) CreateFarmMarket(ctx context.Context, args db.CreateFarmMarketParams) (*model.Market, error) {
	market, err := r.queries.CreateFarmMarket(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:           market.ID,
		Name:         market.Product,
		Image:        market.Image,
		Tag:          market.Tag,
		Volume:       int(market.Volume),
		PricePerUnit: int(market.PricePerUnit),
		CreatedAt:    market.CreatedAt,
		UpdatedAt:    market.UpdatedAt,
	}, nil
}
