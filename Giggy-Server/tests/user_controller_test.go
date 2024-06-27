package tests

import (
	"context"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestUserController(t *testing.T) {
	ctx := context.Background()
	signinC := signinController()
	userC := userController()
	user, err := signinC.CreateUserByPhone(ctx, phone, avatar)
	assert.Nil(t, err)
	assert.NotNil(t, user)

	defer func() {
		queries.ClearTestUsers(ctx)
	}()

	t.Run("grant_farming_rights", func(t *testing.T) {
		u, err := userC.SetFarmingRights(ctx, db.SetFarmingRightsParams{ID: user.ID, HasFarmingRights: true})
		assert.Nil(t, err)
		assert.True(t, u.HasFarmingRights, "should be true")
	})

	t.Run("revoke_farming_rights", func(t *testing.T) {
		u, err := userC.SetFarmingRights(ctx, db.SetFarmingRightsParams{ID: user.ID, HasFarmingRights: false})
		assert.Nil(t, err)
		assert.False(t, u.HasFarmingRights, "shoule be false")
	})
}
