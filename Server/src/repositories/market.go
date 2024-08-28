package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/google/uuid"
)

type MarketRepository struct {
	store postgres.Store
}

func (r *MarketRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *MarketRepository) GetMarketsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Market, error) {
	var markets []*model.Market
	ps, err := r.store.StoreReader.GetMarketsBelongingToFarm(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range ps {
		market := &model.Market{
			ID:            item.ID,
			Name:          item.Product,
			Image:         item.Image,
			FarmID:        item.FarmID,
			Status:        model.MarketStatus(item.Status),
			Unit:          item.Unit,
			Volume:        int(item.Volume),
			RunningVolume: int(item.RunningVolume),
			PricePerUnit:  int(item.PricePerUnit),
			CreatedAt:     item.CreatedAt,
			UpdatedAt:     item.UpdatedAt,
		}

		markets = append(markets, market)
	}

	return markets, nil
}

func (r *MarketRepository) GetMarketByID(ctx context.Context, id uuid.UUID) (*model.Market, error) {
	p, err := r.store.StoreReader.GetMarketByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:            p.ID,
		Name:          p.Product,
		Image:         p.Image,
		Unit:          p.Unit,
		FarmID:        p.FarmID,
		Volume:        int(p.Volume),
		Details:       p.Details,
		RunningVolume: int(p.RunningVolume),
		PricePerUnit:  int(p.PricePerUnit),
		CreatedAt:     p.CreatedAt,
		UpdatedAt:     p.UpdatedAt,
	}, nil
}

func (r *MarketRepository) CreateFarmMarket(ctx context.Context, args db.CreateFarmMarketParams) (*model.Market, error) {
	market, err := r.store.StoreWriter.CreateFarmMarket(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:            market.ID,
		Name:          market.Product,
		Image:         market.Image,
		Unit:          market.Unit,
		Details:       market.Details,
		Tag:           market.Tag,
		FarmID:        market.FarmID,
		Status:        model.MarketStatus(market.Status),
		Volume:        int(market.Volume),
		RunningVolume: int(market.RunningVolume),
		PricePerUnit:  int(market.PricePerUnit),
		CreatedAt:     market.CreatedAt,
		UpdatedAt:     market.UpdatedAt,
	}, nil
}

func (r *MarketRepository) GetLocalizedHarvestMarkets(ctx context.Context, userID uuid.UUID, args db.GetLocalizedHarvestMarketsParams) ([]*model.Market, error) {
	var markets []*model.Market
	m, err := r.store.StoreReader.GetLocalizedHarvestMarkets(ctx, args)
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
			ID:            item.ID,
			Name:          item.Product,
			Image:         item.Image,
			Unit:          item.Unit,
			Volume:        int(item.Volume),
			Details:       item.Details,
			RunningVolume: int(item.RunningVolume),
			CanOrder:      farmOwner != userID.String(),
			FarmID:        item.FarmID,
			PricePerUnit:  int(item.PricePerUnit),
			CreatedAt:     item.CreatedAt,
			UpdatedAt:     item.UpdatedAt,
		}
		markets = append(markets, market)
	}

	return markets, nil
}

func (r *MarketRepository) getFarmOwnerID(ctx context.Context, farmID uuid.UUID) (string, error) {
	owner, err := r.store.StoreReader.GetFarmOwnerID(ctx, farmID)
	if err != nil {
		return "", err
	}

	return owner.String(), nil
}

func (r *MarketRepository) SetMarketStatus(ctx context.Context, args db.SetMarketStatusParams) (*model.Market, error) {
	m, err := r.store.StoreWriter.SetMarketStatus(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:     m.ID,
		Status: model.MarketStatus(m.Status),
	}, nil
}
