package controllers

import (
	"context"
	"errors"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type OrderController struct {
	r *repositories.OrderRepository
}

func (c *OrderController) Init(queries *db.Queries) {
	c.r = &repositories.OrderRepository{}
	c.r.Init(queries)
}

func (c *OrderController) GetOrdersBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Order, error) {
	return c.r.GetOrdersBelongingToFarm(ctx, id)
}

func (c *OrderController) CreateOrder(ctx context.Context, args db.CreateOrderParams) (*model.Order, error) {
	return c.r.CreateOrder(ctx, args)
}

func (c *OrderController) GetOrdersBelongingToUser(ctx context.Context, userID uuid.UUID) ([]*model.Order, error) {
	return c.r.GetOrdersBelongingToUser(ctx, userID)
}

func (c *OrderController) SendOrderToFarm(ctx context.Context, userID uuid.UUID, orders []*model.SendOrderToFarmInput) (bool, error) {
	for _, item := range orders {
		if supplyExists := c.r.MarketHasSupply(ctx, item.MarketID); !supplyExists {
			continue
		}
		_, err := c.CreateOrder(ctx, db.CreateOrderParams{
			Volume:     int32(item.Volume),
			ToBePaid:   int32(item.ToBePaid),
			Currency:   item.Currency,
			CustomerID: userID,
			MarketID:   item.MarketID,
			FarmID:     item.FarmID,
		})
		if err != nil {
			return false, err
		}
		if _, err := c.r.UpdateMarketSupply(ctx, db.UpdateMarketVolumeParams{
			ID:     item.MarketID,
			Volume: int32(item.Volume),
		}); err != nil {
			return false, err
		}

		if b := c.r.DeleteCartItemFromOrder(ctx, item.CartID); !b {
			return false, errors.New("order: delete cart item from order")
		}
	}
	return true, nil
}

func (c *OrderController) GetUserOrdersCount(ctx context.Context, userID uuid.UUID) (int, error) {
	return c.r.GetUserOrdersCount(ctx, userID)
}
