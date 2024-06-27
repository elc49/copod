package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
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

func (c *UserController) GetUserByID(ctx context.Context, id uuid.UUID) (*model.User, error) {
	return c.r.GetUserByID(ctx, id)
}

func (c *UserController) SetFarmingRights(ctx context.Context, args db.SetFarmingRightsParams) (*model.User, error) {
	return c.r.SetFarmingRights(ctx, args)
}

func (c *UserController) SetPosterRights(ctx context.Context, args db.SetPosterRightsParams) (*model.User, error) {
	return c.r.SetPosterRights(ctx, args)
}
