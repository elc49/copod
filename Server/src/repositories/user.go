package repositories

import (
	"context"

	"github.com/elc49/vuno/Server/src/postgres"
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
