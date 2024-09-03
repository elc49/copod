package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres/db"
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
		store.StoreWriter.ClearTestFarms(ctx)
		store.StoreWriter.ClearTestUsers(ctx)
		store.StoreWriter.ClearTestCarts(ctx)
		store.StoreWriter.ClearTestMarkets(ctx)
	}()

	createMarket := func(ctx context.Context) (*model.Market, error) {
		store.StoreWriter.ClearTestMarkets(ctx)
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

	t.Run("get_user_cart_items", func(t *testing.T) {
		market, _ := createMarket(ctx)
		_, err := cartC.AddToCart(ctx, db.AddToCartParams{
			Volume:   14,
			UserID:   user.ID,
			MarketID: market.ID,
			FarmID:   farm.ID,
		})
		assert.Nil(t, err)

		c, err := cartC.GetUserCartItems(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(c), 1)
	})

	t.Run("delete_cart_item", func(t *testing.T) {
		market, _ := createMarket(ctx)
		c, err := cartC.AddToCart(ctx, db.AddToCartParams{
			Volume:   14,
			UserID:   user.ID,
			MarketID: market.ID,
			FarmID:   farm.ID,
		})
		assert.Nil(t, err)

		b, err := cartC.DeleteCartItem(ctx, c.MarketID)
		assert.Nil(t, err)
		assert.True(t, b)

		uc, err := cartC.GetUserCartItems(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(uc), 0)
	})
}
