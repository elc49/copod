package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type SessionRepository struct {
	db *db.Queries
}

func (sr *SessionRepository) Init(queries *db.Queries) {
	sr.db = queries
}

func (sr *SessionRepository) CreateSessionByPhone(ctx context.Context, args db.CreateSessionByPhoneParams) (*model.Session, error) {
	newSession, err := sr.db.CreateSessionByPhone(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Session{
		ID:     newSession.ID,
		UserID: newSession.UserID,
	}, nil
}

func (sr *SessionRepository) GetSessionByUserID(ctx context.Context, userID uuid.UUID) (*model.Session, error) {
	session, err := sr.db.GetSessionByUserID(ctx, userID)
	if err == sql.ErrNoRows {
		// No active sessions
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Session{
		ID:     session.ID,
		UserID: session.UserID,
	}, nil
}
