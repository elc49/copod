package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/util"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type SigninRepository struct {
	db *db.Queries
}

func (mbs *SigninRepository) Init(queries *db.Queries) {
	mbs.db = queries
}

func (mbs *SigninRepository) CreateUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	args := db.CreateUserByPhoneParams{
		Phone:    phone,
		Username: sql.NullString{String: util.RandomStringByLength(5), Valid: true},
	}
	newUser, err := mbs.db.CreateUserByPhone(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.User{
		ID:    newUser.ID,
		Phone: newUser.Phone,
	}, nil
}

func (mbs *SigninRepository) GetUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	user, err := mbs.db.GetUserByPhone(ctx, phone)
	if err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.User{
		ID:    user.ID,
		Phone: user.Phone,
	}, nil
}

func (mbs *SigninRepository) GetUserByID(ctx context.Context, ID uuid.UUID) (*model.User, error) {
	user, err := mbs.db.GetUserByID(ctx, ID)
	if err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.User{
		ID: user.ID,
	}, nil
}
