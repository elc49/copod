package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type OrderRepository struct {
	db *db.Queries
}

func (r *OrderRepository) Init(db *db.Queries) {
	r.db = db
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
		Volume:     int(order.Volume),
		CustomerID: order.CustomerID,
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
			CustomerID: item.CustomerID,
			MarketID:   item.MarketID,
			CreatedAt:  item.CreatedAt,
			UpdatedAt:  item.UpdatedAt,
		}

		orders = append(orders, order)
	}

	return orders, nil
}
