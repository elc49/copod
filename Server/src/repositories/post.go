package repositories

import (
	"context"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/elc49/vuno/Server/src/util"
)

type PostRepository struct {
	store postgres.Store
}

func (r *PostRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *PostRepository) CreatePost(ctx context.Context, args db.CreatePostParams) (*model.Post, error) {
	newPost, err := r.store.StoreWriter.CreatePost(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Post{
		ID:        newPost.ID,
		Text:      newPost.Text,
		CreatedAt: newPost.CreatedAt,
		UpdatedAt: newPost.UpdatedAt,
	}, nil
}

func (r *PostRepository) GetLocalizedPosters(ctx context.Context, args db.GetLocalizedPostersParams) ([]*model.Post, error) {
	var posts []*model.Post
	p, err := r.store.StoreReader.GetLocalizedPosters(ctx, args)
	if err != nil {
		return nil, err
	}

	for _, item := range p {
		gps := util.ParsePostgisLocation(item.Location)
		post := &model.Post{
			ID:    item.ID,
			Text:  item.Text,
			Tags:  item.Tags,
			Image: item.Image,
			FarmAddress: &model.Address{
				AddressString: item.AddressString,
				Coords:        gps.Coords,
			},
			UserID:    item.UserID,
			CreatedAt: item.CreatedAt,
			UpdatedAt: item.UpdatedAt,
		}
		posts = append(posts, post)
	}

	return posts, nil
}
