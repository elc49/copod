package tests

import (
	"context"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestFarmController(t *testing.T) {
	ctx := context.Background()
	signinC := signinController()
	farmC := farmController()
	user, err := signinC.CreateUserByPhone(ctx, phone, avatar)
	assert.Nil(t, err)
	assert.NotNil(t, user)

	defer func() {
		queries.ClearTestFarms(ctx)
		queries.ClearTestUsers(ctx)
	}()

	t.Run("create_farm", func(t *testing.T) {
		farm, err := farmC.CreateFarm(ctx, db.CreateFarmParams{
			Name:      "Agro-dealers",
			Thumbnail: avatar,
			UserID:    user.ID,
		})
		assert.Nil(t, err)
		assert.NotNil(t, farm)
	})

	t.Run("get_farms_belonging_to_user", func(t *testing.T) {
		farms, err := farmC.GetFarmsBelongingToUser(ctx, user.ID)
		assert.Nil(t, err)
		assert.Equal(t, len(farms), 1)
	})

	t.Run("get_farm_by_its_id", func(t *testing.T) {
		queries.ClearTestFarms(ctx)
		f, err := farmC.CreateFarm(ctx, db.CreateFarmParams{
			Name:      "Agro-dealers",
			Thumbnail: avatar,
			UserID:    user.ID,
		})
		assert.Nil(t, err)

		farm, err := farmC.GetFarmByID(ctx, f.ID)
		assert.Nil(t, err)
		assert.NotNil(t, farm)
		assert.Equal(t, farm.Name, "Agro-dealers")
	})
}
