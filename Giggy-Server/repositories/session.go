package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

type MobileSessionRepository struct {
	db *db.Queries
}

func (mbs *MobileSessionRepository) Init(queries *db.Queries) {
	mbs.db = queries
}

func (mbs *MobileSessionRepository) CreateSessionByPhone(ctx context.Context, args db.CreateSessionByPhoneParams) (*model.Session, error) {
	newSession, err := mbs.db.CreateSessionByPhone(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Session{
		ID:     newSession.ID,
		UserID: newSession.UserID,
	}, nil
}
