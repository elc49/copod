package tests

import (
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestSigninController(t *testing.T) {
	signinC := signinController()
	ctx := context.Background()

	t.Run("create_user_by_phone", func(t *testing.T) {
		phone := "254791215745"
		user, err := signinC.CreateUserByPhone(ctx, phone)

		assert.Nil(t, err)
		assert.NotNil(t, user)
		assert.Equal(t, user.Phone, phone)
	})
}
