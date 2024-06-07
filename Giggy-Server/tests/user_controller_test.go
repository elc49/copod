package tests

import (
	"context"
	"testing"
)

func TestUserController(t *testing.T) {
	userController := userController()
	ctx := context.Background()

	t.Run("count_users", func(t *testing.T) {
		userController.CountUsers(ctx)
	})
}
