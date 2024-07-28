package controllers

import (
	"context"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/elc49/vuno/Server/src/repositories"
	"github.com/google/uuid"
)

type SigninController struct {
	r *repositories.SigninRepository
}

func (mbsc *SigninController) Init(queries *db.Queries) {
	mbsc.r = &repositories.SigninRepository{}
	mbsc.r.Init(queries)
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
