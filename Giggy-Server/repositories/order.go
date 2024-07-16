package repositories

import (
	"context"
	"sync"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type OrderRepository struct {
	db *db.Queries
	mu sync.Mutex
}

func (r *OrderRepository) Init(db *db.Queries) {
	r.db = db
	r.mu = sync.Mutex{}
}

func (r *OrderRepository) GetOrdersBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Order, error) {
	var orders []*model.Order
	os, err := r.db.GetOrdersBelongingToFarm(ctx, id)
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
	order, err := r.db.CreateOrder(ctx, args)
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
	o, err := r.db.GetOrdersBelongingToUser(ctx, userID)
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

	market, err := r.db.GetMarketByID(ctx, args.ID)
	if err != nil {
		return nil, err
	}

	args.Volume = market.Volume - args.Volume
	m, err := r.db.UpdateMarketVolume(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Market{
		ID:        m.ID,
		Volume:    int(m.Volume),
		FarmID:    m.FarmID,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}, nil
}

func (r *OrderRepository) MarketHasSupply(ctx context.Context, marketID uuid.UUID) bool {
	r.mu.Lock()
	defer r.mu.Unlock()

	m, _ := r.db.GetMarketByID(ctx, marketID)
	return m.Volume != 0
}

func (r *OrderRepository) DeleteCartItemFromOrder(ctx context.Context, cartID uuid.UUID) bool {
	if err := r.db.DeleteCartItem(ctx, cartID); err != nil {
		return false
	}

	return true
}

func (r *OrderRepository) GetUserOrdersCount(ctx context.Context, userID uuid.UUID) (int, error) {
	c, err := r.db.GetUserOrdersCount(ctx, userID)
	if err != nil {
		return 0, nil
	}

	return int(c), nil
}
