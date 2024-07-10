package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
)

type UserController struct {
	r *repositories.UserRepository
}

func (c *UserController) Init(queries *db.Queries) {
	c.r = &repositories.UserRepository{}
	c.r.Init(queries)
}

func (c *UserController) CountUsers(ctx context.Context) (int, error) {
	return c.r.CountUsers(ctx)
}
