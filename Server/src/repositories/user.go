package repositories

import (
	"context"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
)

type UserRepository struct {
	store postgres.Store
}

func (r *UserRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *UserRepository) CountUsers(ctx context.Context) (int, error) {
	count, err := r.store.StoreReader.CountUsers(ctx)
	if err != nil {
		return 0, err
	}

	return int(count), nil
}

func (r *UserRepository) SetUserNotificationTrackingID(ctx context.Context, args db.SetUserNotificationTrackingIDParams) (*model.User, error) {
	user, err := r.store.StoreWriter.SetUserNotificationTrackingID(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.User{
		ID:                     user.ID,
		NotificationTrackingID: &user.NotificationTrackingID.String,
	}, nil
}
