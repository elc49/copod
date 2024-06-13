package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
)

type PostController struct {
	r *repositories.PostRepository
}

func (c PostController) Init(queries *db.Queries) {
	r := &repositories.PostRepository{}
	r.Init(queries)
}

func (c PostController) CreatePost(ctx context.Context, args db.CreatePostParams) (*model.Post, error) {
	return c.r.CreatePost(ctx, args)
}
