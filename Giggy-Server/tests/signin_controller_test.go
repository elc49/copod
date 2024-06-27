package tests

import (
	"context"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/stretchr/testify/assert"
)

func TestSigninController(t *testing.T) {
	ctx := context.Background()
	var u *model.User
	signinC := signinController()

	defer func() {
		queries.ClearTestUsers(ctx)
	}()

	t.Run("create_user_by_phone", func(t *testing.T) {
		var err error
		u, err = signinC.CreateUserByPhone(ctx, phone, avatar)

		assert.Nil(t, err)
		assert.Equal(t, u.Phone, phone, "phones should be equal")
	})

	t.Run("get_user_by_phone", func(t *testing.T) {
		var err error
		u, err = signinC.GetUserByPhone(ctx, phone)

		assert.Nil(t, err)
		assert.NotNil(t, u, "should have user object")
	})

	t.Run("get_user_by_id", func(t *testing.T) {
		var err error
		u, err = signinC.GetUserByID(ctx, u.ID)

		assert.Nil(t, err)
		assert.NotNil(t, u, "should have user object")
	})
}
