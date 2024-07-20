package tests

import (
	"context"
	"fmt"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
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
		p, err := postC.CreatePost(ctx, user.ID, model.NewPostInput{
			Text:     "Shiny evening here at the farm",
			Image:    avatar,
			Tags:     []string{"animal feed", "farm inputs"},
			Location: &model.GpsInput{Lng: 36.1289, Lat: -1.2743},
		})
		assert.Nil(t, err)
		assert.NotNil(t, p)
		assert.Equal(t, p.Text, "Shiny evening here at the farm")
	})

	t.Run("get_localized_posters", func(t *testing.T) {
		p, err := postC.GetLocalizedPosters(ctx, db.GetLocalizedPostersParams{
			Point:  fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", 36.1289, -1.2743),
			Radius: 500,
		})
		assert.Nil(t, err)
		assert.True(t, len(p) == 1)
	})
}
