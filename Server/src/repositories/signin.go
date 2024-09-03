package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/util"
	"github.com/google/uuid"
)

type SigninRepository struct {
	store postgres.Store
}

func (mbs *SigninRepository) Init(store postgres.Store) {
	mbs.store = store
}

func (mbs *SigninRepository) CreateUserByPhone(ctx context.Context, phone, avatar string) (*model.User, error) {
	args := db.CreateUserByPhoneParams{
		Phone:    phone,
		Username: sql.NullString{String: util.RandomStringByLength(5), Valid: true},
		Avatar:   avatar,
	}
	newUser, err := mbs.store.StoreWriter.CreateUserByPhone(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.User{
		ID:               newUser.ID,
		Phone:            newUser.Phone,
		HasFarmingRights: newUser.HasFarmingRights,
		HasPosterRights:  newUser.HasPosterRights,
	}, nil
}

func (mbs *SigninRepository) GetUserByPhone(ctx context.Context, phone string) (*model.User, error) {
	user, err := mbs.store.StoreReader.GetUserByPhone(ctx, phone)
	if err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.User{
		ID:               user.ID,
		Phone:            user.Phone,
		HasFarmingRights: user.HasFarmingRights,
		HasPosterRights:  user.HasPosterRights,
	}, nil
}

func (mbs *SigninRepository) GetUserByID(ctx context.Context, ID uuid.UUID) (*model.User, error) {
	user, err := mbs.store.StoreReader.GetUserByID(ctx, ID)
	if err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.User{
		ID:               user.ID,
		Phone:            user.Phone,
		Avatar:           user.Avatar,
		HasFarmingRights: user.HasFarmingRights,
		HasPosterRights:  user.HasPosterRights,
		Username:         user.Username.String,
	}, nil
}
