package controllers

import (
	"context"
	"errors"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/repositories"
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
		// Create order
		order, err := c.createOrder(ctx, db.CreateOrderParams{
			ToBePaid:   int32(item.ToBePaid),
			Currency:   item.Currency,
			CustomerID: userID,
			FarmID:     item.FarmID,
		})
		if err != nil {
			return false, err
		}

		for _, orderItem := range item.OrderItems {
			// Does supply exist to cover order
			if supplyExists := c.r.MarketHasSupply(ctx, orderItem.MarketID, orderItem.Volume); !supplyExists {
				continue
			}
			// Create order item
			orderItemArgs := db.CreateOrderItemParams{
				OrderID:  order.ID,
				MarketID: orderItem.MarketID,
				Volume:   int32(orderItem.Volume),
			}
			if _, err := c.r.CreateOrderItem(ctx, orderItemArgs); err != nil {
				return false, err
			}

			// Update product market supply
			if _, err := c.r.UpdateMarketSupply(ctx, db.UpdateMarketVolumeParams{
				ID:            orderItem.MarketID,
				RunningVolume: int32(orderItem.Volume),
			}); err != nil {
				return false, err
			}

			// Delete item from cart
			if b := c.r.DeleteOrderFromCart(ctx, orderItem.MarketID); !b {
				return false, errors.New("order: delete cart item from order")
			}
		}
		// Cleanup blank orders
		if c.r.OrderHasItems(ctx, order.ID) {
			c.r.DeleteOrder(ctx, order.ID)
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

func (c *OrderController) CompletedFarmOrders(ctx context.Context, args db.CompletedFarmOrdersParams) (int, error) {
	count, err := c.r.CompletedFarmOrders(ctx, args)
	if err != nil {
		return 0, err
	}

	return *count, nil
}

func (c *OrderController) GetOrderItems(ctx context.Context, orderID uuid.UUID) ([]*model.OrderItem, error) {
	return c.r.GetOrderItems(ctx, orderID)
}
