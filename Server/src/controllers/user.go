package controllers

import (
	"context"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/repositories"
)

var usc UserControllerInterface

type UserControllerInterface interface {
	CountUsers(context.Context) (int, error)
	SetUserNotificationTrackingID(context.Context, db.SetUserNotificationTrackingIDParams) (*model.User, error)
}

var _ UserControllerInterface = (*UserController)(nil)

type UserController struct {
	r *repositories.UserRepository
}

func (c *UserController) Init(store postgres.Store) {
	c.r = &repositories.UserRepository{}
	c.r.Init(store)
	usc = c
}

func GetUserController() UserControllerInterface {
	return usc
}

func (c *UserController) CountUsers(ctx context.Context) (int, error) {
	return c.r.CountUsers(ctx)
}

func (c *UserController) SetUserNotificationTrackingID(ctx context.Context, args db.SetUserNotificationTrackingIDParams) (*model.User, error) {
	return c.r.SetUserNotificationTrackingID(ctx, args)
}
