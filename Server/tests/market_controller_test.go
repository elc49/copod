package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestMarketController(t *testing.T) {
	ctx := context.Background()
	signinC := signinController()
	user, _ := signinC.CreateUserByPhone(ctx, phone, avatar)
	marketC := marketController()
	farmC := farmController()
	orderC := orderController()
	cartC := cartController()
	farm, _ := farmC.CreateFarm(ctx, db.CreateFarmParams{
		Name:      "Agro-dealers",
		Thumbnail: avatar,
		UserID:    user.ID,
	})

	createMarket := func(ctx context.Context) (*model.Market, error) {
		store.StoreWriter.ClearTestMarkets(ctx)
		return marketC.CreateFarmMarket(ctx, db.CreateFarmMarketParams{
			Product:      "Guavas",
			Image:        avatar,
			Tag:          "seeds",
			Location:     fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
			Unit:         "kg",
			Details:      "Fresh from farm",
			Type:         "HARVEST",
			Volume:       120,
			PricePerUnit: 5,
			FarmID:       farm.ID,
		})
	}

	defer func() {
		store.StoreWriter.ClearTestMarkets(ctx)
		store.StoreWriter.ClearTestFarms(ctx)
		store.StoreWriter.ClearTestUsers(ctx)
		store.StoreWriter.ClearTestOrders(ctx)
		store.StoreWriter.ClearTestCarts(ctx)
	}()

	t.Run("create_farm_market", func(t *testing.T) {
		m, err := createMarket(ctx)
		assert.Nil(t, err)
		assert.Equal(t, m.Volume, m.RunningVolume)
		assert.Equal(t, m.Details, "Fresh from farm")
	})

	t.Run("get_farm_market_by_its_id", func(t *testing.T) {
		m, _ := createMarket(ctx)

		fM, err := marketC.GetMarketByID(ctx, m.ID)
		assert.Nil(t, err)
		assert.NotNil(t, fM)
		assert.Equal(t, fM.Name, "Guavas")
	})

	t.Run("get_markets_belonging_to_farm", func(t *testing.T) {
		createMarket(ctx)

		mrkts, err := marketC.GetMarketsBelongingToFarm(ctx, farm.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(mrkts), 1)
	})

	t.Run("get_nearby_markets", func(t *testing.T) {
		createMarket(ctx)

		mrkts, err := marketC.GetLocalizedMarkets(ctx, user.ID, db.GetLocalizedMarketsParams{
			Point:  fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
			Radius: 2000,
		})
		assert.Nil(t, err)
		assert.True(t, len(mrkts) == 1)
		assert.False(t, mrkts[0].CanOrder, false)
	})

	t.Run("get_nearby_markets_without_0_supply", func(t *testing.T) {
		createMarket(ctx)

		mrkts, _ := marketC.GetLocalizedMarkets(ctx, user.ID, db.GetLocalizedMarketsParams{
			Point:  fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
			Radius: 2000,
		})
		cart, _ := cartC.AddToCart(ctx, db.AddToCartParams{
			Volume:   4,
			UserID:   user.ID,
			MarketID: mrkts[0].ID,
			FarmID:   farm.ID,
		})
		order := []*model.SendOrderToFarmInput{
			{
				CartID:   cart.ID,
				Volume:   120,
				ToBePaid: 200,
				Currency: "KES",
				MarketID: mrkts[0].ID,
				FarmID:   farm.ID,
			},
		}
		// Place order and exhaust supply
		orderC.SendOrderToFarm(ctx, user.ID, order)
		// Get markets
		mrkts, _ = marketC.GetLocalizedMarkets(ctx, user.ID, db.GetLocalizedMarketsParams{
			Point:  fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
			Radius: 2000,
		})
		assert.Equal(t, len(mrkts), 0)
	})

	t.Run("close_market", func(t *testing.T) {
		m, _ := createMarket(ctx)
		r, err := marketC.SetMarketStatus(ctx, db.SetMarketStatusParams{
			ID:     m.ID,
			Status: model.MarketStatusClosed.String(),
		})
		assert.Nil(t, err)
		assert.Equal(t, r.Status, model.MarketStatusClosed)
	})

	t.Run("open_market", func(t *testing.T) {
		m, _ := createMarket(ctx)
		r, err := marketC.SetMarketStatus(ctx, db.SetMarketStatusParams{
			ID:     m.ID,
			Status: model.MarketStatusOpen.String(),
		})
		assert.Nil(t, err)
		assert.Equal(t, r.Status, model.MarketStatusOpen)
	})
}
