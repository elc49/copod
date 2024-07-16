package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
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
		queries.ClearTestUsers(ctx)
		queries.ClearTestFarms(ctx)
		queries.ClearTestMarkets(ctx)
		queries.ClearTestOrders(ctx)
	}()

	t.Run("create_order", func(t *testing.T) {
		order, err := orderC.CreateOrder(ctx, db.CreateOrderParams{
			Volume:     1,
			ToBePaid:   200,
			Currency:   "KES",
			CustomerID: user.ID,
			MarketID:   market.ID,
			FarmID:     farm.ID,
		})
		assert.Nil(t, err)
		assert.Equal(t, order.Volume, 1)
		assert.Equal(t, order.Currency, "KES")
	})

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
		assert.NotEqual(t, market.Volume, m.Volume)
	})

	t.Run("get_orders_belonging_to_user", func(t *testing.T) {
		orders, err := orderC.GetOrdersBelongingToUser(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(orders), 2)
	})

	t.Run("get_user_orders_count", func(t *testing.T) {
		c, err := orderC.GetUserOrdersCount(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, c, 2)
	})
}
