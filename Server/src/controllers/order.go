package controllers

import (
	"context"
	"errors"

	"firebase.google.com/go/messaging"
	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/fcm"
	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/repositories"
	"github.com/google/uuid"
	"github.com/sirupsen/logrus"
)

type OrderController struct {
	r   *repositories.OrderRepository
	log *logrus.Logger
}

func (c *OrderController) Init(store postgres.Store) {
	c.r = &repositories.OrderRepository{}
	c.r.Init(store)
	c.log = logger.GetLogger()
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

func (c *OrderController) SendOrderToFarm(ctx context.Context, userID uuid.UUID, orderInput model.SendOrderToFarmInput) (*model.Order, error) {
	// Create order
	var order *model.Order
	var err error
	orderCreated := false

	for _, orderItem := range orderInput.OrderItems {
		if !orderCreated {
			order, err = c.createOrder(ctx, db.CreateOrderParams{
				ToBePaid:   int32(orderInput.ToBePaid),
				Currency:   orderInput.Currency,
				CustomerID: userID,
				FarmID:     orderItem.FarmID,
			})
			if err != nil {
				return nil, err
			}
			orderCreated = true
		}
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
			return nil, err
		}

		// Update product market supply
		if _, err := c.r.UpdateMarketSupply(ctx, db.UpdateMarketVolumeParams{
			ID:            orderItem.MarketID,
			RunningVolume: int32(orderItem.Volume),
		}); err != nil {
			return nil, err
		}

		// Delete item from cart
		if b := c.r.DeleteOrderFromCart(ctx, orderItem.MarketID); !b {
			return nil, errors.New("order: delete cart item from order")
		}
		// Notify farm owner
		if config.Configuration != nil {
			notifyService := fcm.GetFCMService()
			go func() {
				uId, err := c.r.GetFarmOwner(ctx, orderItem.FarmID)
				if err != nil {
					c.log.WithFields(logrus.Fields{"farmId": orderItem.FarmID}).WithError(err).Errorf("order controller: GetFarmOwner")
					return
				}
				token, tokenErr := c.r.GetFarmOwnerNotificationTrackingID(context.Background(), *uId)
				if tokenErr != nil {
					c.log.WithFields(logrus.Fields{"userId": *uId}).WithError(tokenErr).Errorf("order controller: GetFarmOwnerNotificationTrackingID")
					return
				}
				msg := &messaging.Message{
					Notification: &messaging.Notification{
						Title: "New order",
						Body:  "You have a new order",
					},
					Token: *token,
				}
				_, err = notifyService.Notify(context.Background(), msg)
				if err != nil {
					return
				}
			}()
		}
	}
	// Cleanup blank order blocks
	if !c.r.OrderHasItems(ctx, order.ID) {
		c.r.DeleteOrder(ctx, order.ID)
	}

	return order, nil
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

func (c *OrderController) GetOrderByID(ctx context.Context, orderID uuid.UUID) (*model.Order, error) {
	return c.r.GetOrderByID(ctx, orderID)
}

func (c *OrderController) GetFarmOwner(ctx context.Context, farmID uuid.UUID) (*uuid.UUID, error) {
	return c.r.GetFarmOwner(ctx, farmID)
}
