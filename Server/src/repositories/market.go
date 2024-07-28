package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/google/uuid"
)

type MarketRepository struct {
	db *db.Queries
}

func (r *MarketRepository) Init(db *db.Queries) {
	r.db = db
}

func (r *MarketRepository) GetMarketsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Market, error) {
	var markets []*model.Market
	ps, err := r.db.GetMarketsBelongingToFarm(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range ps {
		market := &model.Market{
			ID:           item.ID,
			Name:         item.Product,
			Image:        item.Image,
			FarmID:       item.FarmID,
			Unit:         item.Unit,
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
	p, err := r.db.GetMarketByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:           p.ID,
		Name:         p.Product,
		Image:        p.Image,
		Unit:         p.Unit,
		FarmID:       p.FarmID,
		Volume:       int(p.Volume),
		PricePerUnit: int(p.PricePerUnit),
		CreatedAt:    p.CreatedAt,
		UpdatedAt:    p.UpdatedAt,
	}, nil
}

func (r *MarketRepository) CreateFarmMarket(ctx context.Context, args db.CreateFarmMarketParams) (*model.Market, error) {
	market, err := r.db.CreateFarmMarket(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:           market.ID,
		Name:         market.Product,
		Image:        market.Image,
		Unit:         market.Unit,
		Tag:          market.Tag,
		FarmID:       market.FarmID,
		Volume:       int(market.Volume),
		PricePerUnit: int(market.PricePerUnit),
		CreatedAt:    market.CreatedAt,
		UpdatedAt:    market.UpdatedAt,
	}, nil
}

func (r *MarketRepository) GetLocalizedMarkets(ctx context.Context, userID uuid.UUID, args db.GetLocalizedMarketsParams) ([]*model.Market, error) {
	var markets []*model.Market
	m, err := r.db.GetLocalizedMarkets(ctx, args)
	if err != nil {
		return nil, err
	}

	for _, item := range m {
		farmOwner, err := r.getFarmOwnerID(ctx, item.FarmID)
		if err != nil {
			return nil, err
		}

		if int(item.Volume) == 0 {
			continue
		}

		market := &model.Market{
			ID:           item.ID,
			Name:         item.Product,
			Image:        item.Image,
			Unit:         item.Unit,
			Volume:       int(item.Volume),
			CanOrder:     farmOwner != userID.String(),
			FarmID:       item.FarmID,
			PricePerUnit: int(item.PricePerUnit),
			CreatedAt:    item.CreatedAt,
			UpdatedAt:    item.UpdatedAt,
		}
		markets = append(markets, market)
	}

	return markets, nil
}

func (r *MarketRepository) getFarmOwnerID(ctx context.Context, farmID uuid.UUID) (string, error) {
	owner, err := r.db.GetFarmOwnerID(ctx, farmID)
	if err != nil {
		return "", err
	}

	return owner.String(), nil
}
