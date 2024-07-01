package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestMarketController(t *testing.T) {
	ctx := context.Background()
	signinC := signinController()
	user, _ := signinC.CreateUserByPhone(ctx, phone, avatar)
	marketC := marketController()
	farmC := farmController()
	farm, _ := farmC.CreateFarm(ctx, db.CreateFarmParams{
		Name:      "Agro-dealers",
		Thumbnail: avatar,
		UserID:    user.ID,
	})

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

	defer func() {
		queries.ClearTestMarkets(ctx)
		queries.ClearTestFarms(ctx)
		queries.ClearTestUsers(ctx)
	}()

	t.Run("create_farm_market", func(t *testing.T) {
		m, err := createMarket(ctx)
		assert.Nil(t, err)
		assert.Equal(t, m.Name, "Guavas")
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

		mrkts, err := marketC.GetNearbyMarkets(ctx, db.GetNearbyMarketsParams{
			Point:  fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1809, -1.2748),
			Radius: 2000,
		})
		assert.Nil(t, err)
		assert.True(t, len(mrkts) > 0)
	})
}
