package repositories

import (
	"context"
	"sync"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/google/uuid"
)

type OrderRepository struct {
	store postgres.Store
	mu    sync.Mutex
}

func (r *OrderRepository) Init(db postgres.Store) {
	r.store = db
	r.mu = sync.Mutex{}
}

func (r *OrderRepository) GetOrdersBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Order, error) {
	var orders []*model.Order
	os, err := r.store.StoreReader.GetOrdersBelongingToFarm(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range os {
		order := &model.Order{
			ID:         item.ID,
			Volume:     int(item.Volume),
			ToBePaid:   int(item.ToBePaid),
			Currency:   item.Currency,
			Status:     model.OrderStatus(item.Status),
			CustomerID: item.CustomerID,
			MarketID:   item.MarketID,
			CreatedAt:  item.CreatedAt,
			UpdatedAt:  item.UpdatedAt,
		}

		orders = append(orders, order)
	}

	return orders, nil
}

func (r *OrderRepository) CreateOrder(ctx context.Context, args db.CreateOrderParams) (*model.Order, error) {
	order, err := r.store.StoreWriter.CreateOrder(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Order{
		ID:         order.ID,
		ToBePaid:   int(order.ToBePaid),
		Currency:   order.Currency,
		Volume:     int(order.Volume),
		CustomerID: order.CustomerID,
		Status:     model.OrderStatus(order.Status),
		MarketID:   order.MarketID,
		CreatedAt:  order.CreatedAt,
		UpdatedAt:  order.UpdatedAt,
	}, nil
}

func (r *OrderRepository) GetOrdersBelongingToUser(ctx context.Context, userID uuid.UUID) ([]*model.Order, error) {
	var orders []*model.Order
	o, err := r.store.StoreReader.GetOrdersBelongingToUser(ctx, userID)
	if err != nil {
		return nil, err
	}

	for _, item := range o {
		order := &model.Order{
			ID:         item.ID,
			Volume:     int(item.Volume),
			ToBePaid:   int(item.ToBePaid),
			Currency:   item.Currency,
			CustomerID: item.CustomerID,
			Status:     model.OrderStatus(item.Status),
			MarketID:   item.MarketID,
			CreatedAt:  item.CreatedAt,
			UpdatedAt:  item.UpdatedAt,
		}

		orders = append(orders, order)
	}

	return orders, nil
}

func (r *OrderRepository) UpdateMarketSupply(ctx context.Context, args db.UpdateMarketVolumeParams) (*model.Market, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	market, err := r.store.StoreReader.GetMarketByID(ctx, args.ID)
	if err != nil {
		return nil, err
	}

	// Take from market running volume
	args.RunningVolume = market.RunningVolume - args.RunningVolume
	m, err := r.store.StoreWriter.UpdateMarketVolume(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:            m.ID,
		RunningVolume: int(m.RunningVolume),
		FarmID:        m.FarmID,
		CreatedAt:     m.CreatedAt,
		UpdatedAt:     m.UpdatedAt,
	}, nil
}

func (r *OrderRepository) MarketHasSupply(ctx context.Context, marketID uuid.UUID, volume int) bool {
	r.mu.Lock()
	defer r.mu.Unlock()

	m, _ := r.store.StoreReader.GetMarketByID(ctx, marketID)
	return m.RunningVolume != 0 && volume <= int(m.RunningVolume)
}

func (r *OrderRepository) DeleteCartItemFromOrder(ctx context.Context, marketID uuid.UUID) bool {
	if err := r.store.StoreWriter.DeleteCartItem(ctx, marketID); err != nil {
		return false
	}

	return true
}

func (r *OrderRepository) GetUserOrdersCount(ctx context.Context, userID uuid.UUID) (int, error) {
	c, err := r.store.StoreReader.GetUserOrdersCount(ctx, userID)
	if err != nil {
		return 0, nil
	}

	return int(c), nil
}

func (r *OrderRepository) UpdateOrderStatus(ctx context.Context, args db.UpdateOrderStatusParams) (*model.Order, error) {
	o, err := r.store.StoreWriter.UpdateOrderStatus(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Order{
		ID:        o.ID,
		Status:    model.OrderStatus(o.Status),
		CreatedAt: o.CreatedAt,
		UpdatedAt: o.UpdatedAt,
	}, nil
}
