package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
)

type SessionController struct {
	r *repositories.MobileSessionRepository
}

func (sc *SessionController) Init(queries *db.Queries) {
	sc.r = &repositories.MobileSessionRepository{}
	sc.r.Init(queries)
}

func (sc *SessionController) CreateSessionByPhone(ctx context.Context, args db.CreateSessionByPhoneParams) (*model.Session, error) {
	return sc.r.CreateSessionByPhone(ctx, args)
}
