package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestOrderController(t *testing.T) {
	ctx := context.Background()
	orderC := orderController()
	signinC := signinController()
	user, _ := signinC.CreateUserByPhone(ctx, phone, avatar)
	farmC := farmController()
	farm, _ := farmC.CreateFarm(ctx, db.CreateFarmParams{
		Name:          "Agro-dealers",
		Thumbnail:     avatar,
		UserID:        user.ID,
		AddressString: "Kajiado",
		Location:      fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
	})
	marketC := marketController()
	market, _ := marketC.CreateFarmMarket(ctx, db.CreateFarmMarketParams{
		Product:      "Guavas",
		Image:        avatar,
		Location:     fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
		Unit:         "Kg",
		Volume:       120,
		PricePerUnit: 5,
		FarmID:       farm.ID,
		Type:         model.MarketTypeHarvest.String(),
	})
	machineryMarket, _ := marketC.CreateFarmMarket(ctx, db.CreateFarmMarketParams{
		Product:      "Tractor",
		Image:        avatar,
		Location:     fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
		Unit:         "Hour",
		Volume:       0,
		PricePerUnit: 1000,
		FarmID:       farm.ID,
		Type:         model.MarketTypeMachinery.String(),
	})
	cartC := cartController()

	defer func() {
		store.StoreWriter.ClearTestUsers(ctx)
		store.StoreWriter.ClearTestFarms(ctx)
		store.StoreWriter.ClearTestMarkets(ctx)
		store.StoreWriter.ClearTestOrders(ctx)
		store.StoreWriter.ClearTestOrderItems(ctx)
	}()

	t.Run("send_order_to_farm", func(t *testing.T) {
		var order *model.Order
		cart, _ := cartC.AddToCart(ctx, db.AddToCartParams{
			Volume:   4,
			UserID:   user.ID,
			MarketID: market.ID,
			FarmID:   farm.ID,
		})
		cart2, _ := cartC.AddToCart(ctx, db.AddToCartParams{
			UserID:   user.ID,
			MarketID: machineryMarket.ID,
			FarmID:   farm.ID,
		})
		orders := model.SendOrderToFarmInput{
			ToBePaid: 2200,
			Currency: "KES",
			OrderItems: []*model.OrderItemInput{
				{
					CartID:   cart.ID,
					FarmID:   farm.ID,
					Volume:   2,
					MarketID: market.ID,
				},
				{
					Volume:   2,
					MarketID: machineryMarket.ID,
					FarmID:   farm.ID,
					CartID:   cart2.ID,
				},
			},
		}
		order, _ = orderC.SendOrderToFarm(ctx, user.ID, orders)
		assert.Equal(t, order.ToBePaid, 2200)
		assert.Equal(t, order.Status, model.OrderStatusPending)

		m, _ := marketC.GetMarketByID(ctx, market.ID)
		assert.NotEqual(t, market.RunningVolume, m.RunningVolume)
		m, _ = marketC.GetMarketByID(ctx, machineryMarket.ID)
		assert.Equal(t, m.Status, model.MarketStatusBooked)

		orderItems, _ := orderC.GetOrderItems(ctx, order.ID)
		assert.Equal(t, len(orderItems), 2)
	})

	t.Run("should_not_accept_order_volume_supply_can't_cover", func(t *testing.T) {
		order := model.SendOrderToFarmInput{
			ToBePaid: 200,
			Currency: "KES",
			OrderItems: []*model.OrderItemInput{
				{
					Volume:   120,
					MarketID: market.ID,
					FarmID:   farm.ID,
				},
				{
					Volume:   2,
					MarketID: machineryMarket.ID,
					FarmID:   farm.ID,
				},
			},
		}
		_, err := orderC.SendOrderToFarm(ctx, user.ID, order)
		assert.Nil(t, err)

		o, err := orderC.GetOrdersBelongingToUser(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(o), 1)
	})

	t.Run("get_orders_belonging_to_user", func(t *testing.T) {
		orders, err := orderC.GetOrdersBelongingToUser(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(orders), 1)
	})

	t.Run("get_user_orders_count", func(t *testing.T) {
		c, err := orderC.GetUserOrdersCount(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, c, 1)
	})

	t.Run("get_farm_orders", func(t *testing.T) {
		orders, err := orderC.GetOrdersBelongingToFarm(ctx, farm.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(orders), 1)
	})
}
