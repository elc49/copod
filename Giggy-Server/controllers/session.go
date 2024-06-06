package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type SessionController struct {
	r *repositories.SessionRepository
}

func (sc *SessionController) Init(queries *db.Queries) {
	sc.r = &repositories.SessionRepository{}
	sc.r.Init(queries)
}

func (sc *SessionController) CreateSessionByPhone(ctx context.Context, args db.CreateSessionByPhoneParams) (*model.Session, error) {
	return sc.r.CreateSessionByPhone(ctx, args)
}

func (sc *SessionController) GetSessionByUserID(ctx context.Context, userID uuid.UUID) (*model.Session, error) {
	return sc.r.GetSessionByUserID(ctx, userID)
}
