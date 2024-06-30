package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
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
