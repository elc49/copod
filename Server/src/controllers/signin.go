package controllers

import (
	"context"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/repositories"
	"github.com/google/uuid"
)

var signinC *SigninController

type SigninInterface interface {
	CreateUserByPhone(context.Context, string, string) (*model.User, error)
	GetUserByPhone(context.Context, string) (*model.User, error)
	GetUserByID(context.Context, uuid.UUID) (*model.User, error)
}

type SigninController struct {
	r *repositories.SigninRepository
}

var _ SigninInterface = (*SigninController)(nil)

func (mbsc *SigninController) Init(store postgres.Store) {
	mbsc.r = &repositories.SigninRepository{}
	mbsc.r.Init(store)
	signinC = mbsc
}

func GetSigninController() *SigninController {
	return signinC
}

func (mbsc *SigninController) CreateUserByPhone(ctx context.Context, phone, avatar string) (*model.User, error) {
	return mbsc.r.CreateUserByPhone(ctx, phone, avatar)
}

func (mbsc *SigninController) GetUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	return mbsc.r.GetUserByPhone(ctx, phone)
}

func (mbsc *SigninController) GetUserByID(ctx context.Context, ID uuid.UUID) (*model.User, error) {
	return mbsc.r.GetUserByID(ctx, ID)
}
