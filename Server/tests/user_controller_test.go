package tests

import (
	"context"
	"database/sql"
	"testing"

	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/google/uuid"
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
		store.StoreWriter.ClearTestUsers(ctx)
	}()

	t.Run("get_user_by_id", func(t *testing.T) {
		u, err := userC.CountUsers(ctx)
		assert.Nil(t, err)
		assert.NotNil(t, u)
		assert.Equal(t, u, 1)
	})

	t.Run("set_notification_tracking_id", func(t *testing.T) {
		u, err := userC.SetUserNotificationTrackingID(ctx, db.SetUserNotificationTrackingIDParams{
			ID:                     user.ID,
			NotificationTrackingID: sql.NullString{String: uuid.New().String(), Valid: true},
		})
		assert.Nil(t, err)
		assert.NotEmpty(t, u.NotificationTrackingID)
	})
}
