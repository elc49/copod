package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type MobileSigninRepository struct {
	db *db.Queries
}

func (mbs *MobileSigninRepository) Init(queries *db.Queries) {
	mbs.db = queries
}

func (mbs *MobileSigninRepository) CreateUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	newUser, err := mbs.db.CreateUserByPhone(ctx, phone)
	if err != nil {
		return nil, err
	}

	return &model.User{
		ID: newUser.ID,
	}, nil
}

func (mbs *MobileSigninRepository) GetUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	user, err := mbs.db.GetUserByPhone(ctx, phone)
	if err != nil {
		return nil, err
	}

	return &model.User{
		ID:    user.ID,
		Phone: user.Phone,
	}, nil
}

func (mbs *MobileSigninRepository) GetUserByID(ctx context.Context, ID uuid.UUID) (*model.User, error) {
	user, err := mbs.db.GetUserByID(ctx, ID)
	if err != nil {
		return nil, err
	}

	return &model.User{
		ID: user.ID,
	}, nil
}
