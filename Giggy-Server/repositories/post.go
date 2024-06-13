package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

type PostRepository struct {
	queries *db.Queries
}

func (r PostRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r PostRepository) CreatePost(ctx context.Context, ars db.CreatePostParams) (*model.Post, error) {
	return nil, nil
}
