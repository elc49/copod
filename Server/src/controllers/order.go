package controllers

import (
	"context"
	"errors"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/elc49/vuno/Server/src/repositories"
	"github.com/google/uuid"
)

type OrderController struct {
	r *repositories.OrderRepository
}

func (c *OrderController) Init(store postgres.Store) {
	c.r = &repositories.OrderRepository{}
	c.r.Init(store)
}

func (c *OrderController) GetOrdersBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Order, error) {
	return c.r.GetOrdersBelongingToFarm(ctx, id)
}

func (c *OrderController) createOrder(ctx context.Context, args db.CreateOrderParams) (*model.Order, error) {
	return c.r.CreateOrder(ctx, args)
}

func (c *OrderController) GetOrdersBelongingToUser(ctx context.Context, userID uuid.UUID) ([]*model.Order, error) {
	return c.r.GetOrdersBelongingToUser(ctx, userID)
}

func (c *OrderController) SendOrderToFarm(ctx context.Context, userID uuid.UUID, orders []*model.SendOrderToFarmInput) (bool, error) {
	for _, item := range orders {
		if supplyExists := c.r.MarketHasSupply(ctx, item.MarketID, item.Volume); !supplyExists {
			continue
		}
		_, err := c.createOrder(ctx, db.CreateOrderParams{
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
			ID:            item.MarketID,
			RunningVolume: int32(item.Volume),
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

func (c *OrderController) UpdateOrderStatus(ctx context.Context, args db.UpdateOrderStatusParams) (*model.Order, error) {
	return c.r.UpdateOrderStatus(ctx, args)
}
