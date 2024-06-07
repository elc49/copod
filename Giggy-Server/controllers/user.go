package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
)

type UserController struct {
	r *repositories.UserRepository
}

func (uc *UserController) Init(queries *db.Queries) {
	uc.r = &repositories.UserRepository{}
	uc.r.Init(queries)
}

func (uc *UserController) CountUsers(ctx context.Context) (int, error) {
	return uc.r.CountUsers(ctx)
}
