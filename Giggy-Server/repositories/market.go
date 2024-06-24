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
	var products []*model.Market
	ps, err := r.queries.GetMarketsBelongingToFarm(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range ps {
		product := &model.Market{
			ID:           item.ID,
			Name:         item.Product,
			Image:        item.Image,
			Volume:       int(item.Volume),
			PricePerUnit: int(item.PricePerUnit),
			CreatedAt:    item.CreatedAt,
			UpdatedAt:    item.UpdatedAt,
		}

		products = append(products, product)
	}

	return products, nil
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
	product, err := r.queries.CreateFarmMarket(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:           product.ID,
		Name:         product.Product,
		Image:        product.Image,
		Volume:       int(product.Volume),
		PricePerUnit: int(product.PricePerUnit),
		CreatedAt:    product.CreatedAt,
		UpdatedAt:    product.UpdatedAt,
	}, nil
}
