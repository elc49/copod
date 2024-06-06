package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

type SessionRepository struct {
	db *db.Queries
}

func (mbs *SessionRepository) Init(queries *db.Queries) {
	mbs.db = queries
}

func (mbs *SessionRepository) CreateSessionByPhone(ctx context.Context, args db.CreateSessionByPhoneParams) (*model.Session, error) {
	newSession, err := mbs.db.CreateSessionByPhone(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Session{
		ID:     newSession.ID,
		UserID: newSession.UserID,
	}, nil
}
