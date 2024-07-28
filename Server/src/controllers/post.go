package controllers

import (
	"context"
	"fmt"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/nominatim"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/elc49/vuno/Server/src/repositories"
	"github.com/google/uuid"
)

type PostController struct {
	r *repositories.PostRepository
}

func (c *PostController) Init(queries *db.Queries) {
	c.r = &repositories.PostRepository{}
	c.r.Init(queries)
}

func (c *PostController) CreatePost(ctx context.Context, userId uuid.UUID, input model.NewPostInput) (*model.Post, error) {
	address, err := nominatim.ReverseGeocode(model.Gps{Lat: input.Location.Lat, Lng: input.Location.Lng})
	if err != nil {
		return nil, err
	}

	args := db.CreatePostParams{
		Text:          input.Text,
		Image:         input.Image,
		Tags:          input.Tags,
		UserID:        userId,
		AddressString: address.AddressString,
		Location:      fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", address.Coords.Lng, address.Coords.Lat),
	}

	return c.r.CreatePost(ctx, args)
}

func (c *PostController) GetLocalizedPosters(ctx context.Context, args db.GetLocalizedPostersParams) ([]*model.Post, error) {
	return c.r.GetLocalizedPosters(ctx, args)
}
