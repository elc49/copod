package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type MobileSigninController struct {
	r *repositories.MobileSigninRepository
}

func (mbsc *MobileSigninController) Init(queries *db.Queries) {
	mbsc.r = &repositories.MobileSigninRepository{}
	mbsc.r.Init(queries)
}

func (mbsc *MobileSigninController) CreateUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	return mbsc.r.CreateUserByPhone(ctx, phone)
}

func (mbsc *MobileSigninController) GetUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	return mbsc.r.GetUserByPhone(ctx, phone)
}

func (mbsc *MobileSigninController) GetUserByID(ctx context.Context, ID uuid.UUID) (*model.User, error) {
	return mbsc.r.GetUserByID(ctx, ID)
}
