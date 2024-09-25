package repositories

import (
	"context"
	"database/sql"
	"sync"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/google/uuid"
	"github.com/sirupsen/logrus"
)

type OrderRepository struct {
	store  postgres.Store
	mu     sync.Mutex
	logger *logrus.Logger
}

func (r *OrderRepository) Init(db postgres.Store) {
	r.store = db
	r.mu = sync.Mutex{}
	r.logger = logger.GetLogger()
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
			ToBePaid:   int(item.ToBePaid),
			ShortID:    item.ShortID,
			Currency:   item.Currency,
			Status:     model.OrderStatus(item.Status),
			CustomerID: item.CustomerID,
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
		CustomerID: order.CustomerID,
		Status:     model.OrderStatus(order.Status),
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
			ToBePaid:   int(item.ToBePaid),
			Currency:   item.Currency,
			CustomerID: item.CustomerID,
			Status:     model.OrderStatus(item.Status),
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

	switch market.Type {
	// Book equipment
	case model.MarketTypeMachinery.String():
		m, err := r.store.StoreWriter.SetMarketStatus(ctx, db.SetMarketStatusParams{
			ID:     market.ID,
			Status: model.MarketStatusBooked.String(),
		})
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
	default:
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

}

func (r *OrderRepository) MarketHasSupply(ctx context.Context, marketID uuid.UUID, volume int) bool {
	r.mu.Lock()
	defer r.mu.Unlock()

	m, _ := r.store.StoreReader.GetMarketByID(ctx, marketID)
	switch model.MarketType(m.Type) {
	case model.MarketTypeMachinery:
		return m.Status == model.MarketStatusOpen.String()
	default:
		return m.RunningVolume != 0 && volume <= int(m.RunningVolume)
	}
}

func (r *OrderRepository) DeleteOrderFromCart(ctx context.Context, marketID uuid.UUID) bool {
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
		ID:         o.ID,
		ToBePaid:   int(o.ToBePaid),
		ShortID:    o.ShortID,
		Currency:   o.Currency,
		Status:     model.OrderStatus(o.Status),
		CustomerID: o.CustomerID,
		CreatedAt:  o.CreatedAt,
		UpdatedAt:  o.UpdatedAt,
	}, nil
}

func (r *OrderRepository) CompletedFarmOrders(ctx context.Context, args db.CompletedFarmOrdersParams) (*int, error) {
	count := 0
	c, err := r.store.StoreReader.CompletedFarmOrders(ctx, args)
	if err != nil {
		return nil, err
	}
	count = int(c)

	return &count, nil
}

func (r *OrderRepository) CreateOrderItem(ctx context.Context, args db.CreateOrderItemParams) (*model.OrderItem, error) {
	o, err := r.store.StoreWriter.CreateOrderItem(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.OrderItem{
		ID:        o.ID,
		Volume:    int(o.Volume),
		MarketID:  o.MarketID,
		CreatedAt: o.CreatedAt,
		UpdatedAt: o.UpdatedAt,
	}, nil
}

func (r *OrderRepository) DeleteOrder(ctx context.Context, orderID uuid.UUID) error {
	err := r.store.StoreWriter.DeleteOrder(ctx, orderID)
	if err != nil {
		return err
	}

	return nil
}

func (r *OrderRepository) OrderHasItems(ctx context.Context, orderID uuid.UUID) bool {
	c, err := r.store.StoreReader.CountOrderItems(ctx, orderID)
	if err != nil {
		r.logger.WithFields(logrus.Fields{"OrderID": orderID}).WithError(err).Errorf("OrderHasItems")
		return false
	}

	return c > 0
}

func (r *OrderRepository) GetOrderItems(ctx context.Context, orderID uuid.UUID) ([]*model.OrderItem, error) {
	var orderItems []*model.OrderItem
	items, err := r.store.StoreReader.GetOrderItems(ctx, orderID)
	if err != nil {
		return nil, err
	}

	for _, item := range items {
		orderItem := &model.OrderItem{
			ID:        item.ID,
			Volume:    int(item.Volume),
			MarketID:  item.MarketID,
			CreatedAt: item.CreatedAt,
			UpdatedAt: item.UpdatedAt,
		}
		orderItems = append(orderItems, orderItem)
	}

	return orderItems, nil
}

func (r *OrderRepository) GetOrderByID(ctx context.Context, orderID uuid.UUID) (*model.Order, error) {
	o, err := r.store.StoreReader.GetOrderByID(ctx, orderID)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Order{
		ID:         o.ID,
		ToBePaid:   int(o.ToBePaid),
		ShortID:    o.ShortID,
		Currency:   o.Currency,
		FarmID:     o.FarmID,
		Status:     model.OrderStatus(o.Status),
		CustomerID: o.CustomerID,
		CreatedAt:  o.CreatedAt,
		UpdatedAt:  o.UpdatedAt,
	}, nil
}
