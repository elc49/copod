package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type SigninController struct {
	r *repositories.SigninRepository
}

func (mbsc *SigninController) Init(queries *db.Queries) {
	mbsc.r = &repositories.SigninRepository{}
	mbsc.r.Init(queries)
}

func (mbsc *SigninController) CreateUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	return mbsc.r.CreateUserByPhone(ctx, phone)
}

func (mbsc *SigninController) GetUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	return mbsc.r.GetUserByPhone(ctx, phone)
}

func (mbsc *SigninController) GetUserByID(ctx context.Context, ID uuid.UUID) (*model.User, error) {
	return mbsc.r.GetUserByID(ctx, ID)
}
