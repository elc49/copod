package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestOrderController(t *testing.T) {
	ctx := context.Background()
	orderC := orderController()
	signinC := signinController()
	user, _ := signinC.CreateUserByPhone(ctx, phone, avatar)
	farmC := farmController()
	farm, _ := farmC.CreateFarm(ctx, db.CreateFarmParams{
		Name:      "Agro-dealers",
		Thumbnail: avatar,
		UserID:    user.ID,
	})
	marketC := marketController()
	market, _ := marketC.CreateFarmMarket(ctx, db.CreateFarmMarketParams{
		Product:      "Guavas",
		Image:        avatar,
		Tag:          "seeds",
		Location:     fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
		Unit:         "kg",
		Volume:       120,
		PricePerUnit: 5,
		FarmID:       farm.ID,
	})

	defer func() {
		store.StoreWriter.ClearTestUsers(ctx)
		store.StoreWriter.ClearTestFarms(ctx)
		store.StoreWriter.ClearTestMarkets(ctx)
		store.StoreWriter.ClearTestOrders(ctx)
	}()

	t.Run("send_order_to_farm", func(t *testing.T) {
		orders := []*model.SendOrderToFarmInput{
			{
				Volume:   2,
				ToBePaid: 200,
				Currency: "KES",
				MarketID: market.ID,
				FarmID:   farm.ID,
			},
		}
		b, err := orderC.SendOrderToFarm(ctx, user.ID, orders)

		assert.Nil(t, err)
		assert.True(t, b)

		m, _ := marketC.GetMarketByID(ctx, market.ID)
		assert.NotEqual(t, market.RunningVolume, m.RunningVolume)
	})

	t.Run("should_not_accept_order_volume_supply_can't_cover", func(t *testing.T) {
		orders := []*model.SendOrderToFarmInput{
			{
				Volume:   120,
				ToBePaid: 200,
				Currency: "KES",
				MarketID: market.ID,
				FarmID:   farm.ID,
			},
		}
		_, err := orderC.SendOrderToFarm(ctx, user.ID, orders)
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
