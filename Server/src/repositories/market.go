package repositories

import (
	"context"
	"database/sql"
	"errors"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/google/uuid"
)

type MarketRepository struct {
	store postgres.Store
}

func (r *MarketRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *MarketRepository) GetMarketsBelongingToFarm(ctx context.Context, arg db.GetMarketsBelongingToFarmParams) ([]*model.Market, error) {
	var markets []*model.Market
	ps, err := r.store.StoreReader.GetMarketsBelongingToFarm(ctx, arg)
	if err != nil {
		return nil, err
	}

	for _, item := range ps {
		market := &model.Market{
			ID:            item.ID,
			Name:          item.Product,
			Image:         item.Image,
			Type:          model.MarketType(item.Type),
			FarmID:        item.FarmID,
			Status:        model.MarketStatus(item.Status),
			Unit:          model.MetricUnit(item.Unit),
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
	if err != nil && errors.Is(err, sql.ErrNoRows) {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:            p.ID,
		Name:          p.Product,
		Image:         p.Image,
		Unit:          model.MetricUnit(p.Unit),
		FarmID:        p.FarmID,
		Volume:        int(p.Volume),
		Details:       p.Details,
		Status:        model.MarketStatus(p.Status),
		Type:          model.MarketType(p.Type),
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
		Unit:          model.MetricUnit(market.Unit),
		Type:          model.MarketType(market.Type),
		Details:       market.Details,
		FarmID:        market.FarmID,
		Status:        model.MarketStatus(market.Status),
		Volume:        int(market.Volume),
		RunningVolume: int(market.RunningVolume),
		PricePerUnit:  int(market.PricePerUnit),
		CreatedAt:     market.CreatedAt,
		UpdatedAt:     market.UpdatedAt,
	}, nil
}

func (r *MarketRepository) GetLocalizedMarkets(ctx context.Context, userID uuid.UUID, args db.GetLocalizedMarketsParams) ([]*model.Market, error) {
	var markets []*model.Market
	m, err := r.store.StoreReader.GetLocalizedMarkets(ctx, args)
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
			Unit:          model.MetricUnit(item.Unit),
			Volume:        int(item.Volume),
			Details:       item.Details,
			Type:          model.MarketType(item.Type),
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

func (r *MarketRepository) GetLocalizedMachineryMarkets(ctx context.Context, userID uuid.UUID, args db.GetLocalizedMachineryMarketsParams) ([]*model.Market, error) {
	var markets []*model.Market
	m, err := r.store.StoreReader.GetLocalizedMachineryMarkets(ctx, args)
	if err != nil {
		return nil, err
	}

	for _, item := range m {
		farmOwner, err := r.getFarmOwnerID(ctx, item.FarmID)
		if err != nil {
			return nil, err
		}

		market := &model.Market{
			ID:           item.ID,
			Name:         item.Product,
			Image:        item.Image,
			Unit:         model.MetricUnit(item.Unit),
			Details:      item.Details,
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
