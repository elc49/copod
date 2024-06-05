package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

type UserRepository struct {
	db *db.Queries
}

func (ur *UserRepository) Init(queries *db.Queries) {
	ur.db = queries
}

func (ur *UserRepository) CountUsers(ctx context.Context) (int, error) {
	count, err := ur.db.CountUsers(ctx)
	if err != nil {
		return 0, err
	}

	return int(count), nil
}
