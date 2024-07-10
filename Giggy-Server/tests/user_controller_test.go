package tests

import (
	"context"
	"testing"

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

	t.Run("get_user_by_id", func(t *testing.T) {
		u, err := userC.CountUsers(ctx)
		assert.Nil(t, err)
		assert.NotNil(t, u)
		assert.Equal(t, u, 1)
	})
}
