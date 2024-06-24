package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type OrderRepository struct {
	queries *db.Queries
}

func (r *OrderRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *OrderRepository) GetOrdersBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Order, error) {
	var orders []*model.Order
	os, err := r.queries.GetOrdersBelongingToFarm(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range os {
		order := &model.Order{
			ID:         item.ID,
			ToBePaid:   int(item.ToBePaid),
			CustomerID: item.CustomerID,
			MarketID:   item.MarketID,
		}

		orders = append(orders, order)
	}

	return orders, nil
}
