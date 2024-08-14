package controllers

import (
	"context"

	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/repositories"
)

type UserController struct {
	r *repositories.UserRepository
}

func (c *UserController) Init(store postgres.Store) {
	c.r = &repositories.UserRepository{}
	c.r.Init(store)
}

func (c *UserController) CountUsers(ctx context.Context) (int, error) {
	return c.r.CountUsers(ctx)
}
