package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/util"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

type PostRepository struct {
	queries *db.Queries
}

func (r *PostRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *PostRepository) CreatePost(ctx context.Context, args db.CreatePostParams) (*model.Post, error) {
	newPost, err := r.queries.CreatePost(ctx, args)
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
	p, err := r.queries.GetLocalizedPosters(ctx, args)
	if err != nil {
		return nil, err
	}

	for _, item := range p {
		post := &model.Post{
			ID:          item.ID,
			Text:        item.Text,
			Tags:        item.Tags,
			Image:       item.Image,
			FarmAddress: util.ParsePostgisLocation(item.Location),
			UserID:      item.UserID,
			CreatedAt:   item.CreatedAt,
			UpdatedAt:   item.UpdatedAt,
		}
		posts = append(posts, post)
	}

	return posts, nil
}
