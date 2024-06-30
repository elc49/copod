package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/stretchr/testify/assert"
)

func TestPostController(t *testing.T) {
	ctx := context.Background()
	postC := postController()
	signinC := signinController()
	user, _ := signinC.CreateUserByPhone(ctx, phone, avatar)

	defer func() {
		queries.ClearTestUsers(ctx)
		queries.ClearTestPosters(ctx)
	}()

	t.Run("create_post", func(t *testing.T) {
		p, err := postC.CreatePost(ctx, db.CreatePostParams{
			Text:     "Shiny evening here at the farm",
			Image:    avatar,
			Tags:     []string{"animal feed", "farm inputs"},
			UserID:   user.ID,
			Location: fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1289, -1.2743),
		})
		assert.Nil(t, err)
		assert.NotNil(t, p)
		assert.Equal(t, p.Text, "Shiny evening here at the farm")
	})
}
