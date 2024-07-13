package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestCartController(t *testing.T) {
	var cart *model.Cart
	ctx := context.Background()
	signinC := signinController()
	user, err := signinC.CreateUserByPhone(ctx, phone, avatar)
	assert.Nil(t, err)
	assert.NotNil(t, user)

	farmC := farmController()
	farm, err := farmC.CreateFarm(ctx, db.CreateFarmParams{
		Name:      "Agro-dealers",
		Thumbnail: avatar,
		UserID:    user.ID,
	})
	marketC := marketController()
	cartC := cartController()

	defer func() {
		queries.ClearTestFarms(ctx)
		queries.ClearTestUsers(ctx)
		queries.ClearTestCarts(ctx)
		queries.ClearTestMarkets(ctx)
	}()

	createMarket := func(ctx context.Context) (*model.Market, error) {
		queries.ClearTestMarkets(ctx)
		return marketC.CreateFarmMarket(ctx, db.CreateFarmMarketParams{
			Product:      "Guavas",
			Image:        avatar,
			Tag:          "seeds",
			Location:     fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
			Unit:         "kg",
			Volume:       120,
			PricePerUnit: 5,
			FarmID:       farm.ID,
		})
	}

	t.Run("add_to_cart", func(t *testing.T) {
		market, _ := createMarket(ctx)
		assert.NotNil(t, market)

		cart, err = cartC.AddToCart(ctx, db.AddToCartParams{
			Volume:   4,
			UserID:   user.ID,
			MarketID: market.ID,
			FarmID:   farm.ID,
		})
		assert.Nil(t, err)
		assert.NotNil(t, cart)
	})

	t.Run("adding_existing_cart_should_update_volume", func(t *testing.T) {
		market, _ := createMarket(ctx)
		u, err := cartC.AddToCart(ctx, db.AddToCartParams{
			Volume:   14,
			UserID:   user.ID,
			MarketID: market.ID,
			FarmID:   farm.ID,
		})
		assert.Nil(t, err)
		assert.NotEqual(t, cart.Volume, u.Volume)
	})
}
