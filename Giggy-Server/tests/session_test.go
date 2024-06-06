package tests

import (
	"context"
	"testing"
	"time"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestSessionController(t *testing.T) {
	var newS *model.Session
	var user *model.User
	ctx := context.Background()
	sessionC := sessionController()
	signinC := signinController()

	defer func() {
		queries.ClearTestUsers(ctx)
		queries.ClearTestSessions(ctx)
	}()

	t.Run("create_new_session", func(t *testing.T) {
		var err error
		// create user
		user, err = signinC.CreateUserByPhone(ctx, phone)
		assert.Nil(t, err)
		assert.NotNil(t, user, "should have user object")

		// create session
		args := db.CreateSessionByPhoneParams{
			Ip:      "127.0.0.1",
			Expires: time.Now().Add(time.Second),
			UserID:  user.ID,
		}
		newS, err = sessionC.CreateSessionByPhone(ctx, args)

		assert.Nil(t, err)
		assert.NotNil(t, newS, "should have session object")
	})

	t.Run("get_active_session", func(t *testing.T) {
		var err error
		newS, err = sessionC.GetSessionByUserID(ctx, user.ID)

		assert.Nil(t, err)
		assert.NotNil(t, newS, "should have session object")
		assert.Equal(t, newS.UserID, user.ID, "user ids should match")
	})

	t.Run("get_expired_session_should_fail", func(t *testing.T) {
		var err error
		time.Sleep(time.Second * 2)
		newS, err = sessionC.GetSessionByUserID(ctx, user.ID)

		assert.Nil(t, err)
		assert.Nil(t, newS, "should have nil session object")
	})

}
