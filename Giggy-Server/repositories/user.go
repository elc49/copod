package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

type UserRepository struct {
	queries *db.Queries
}

func (r *UserRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *UserRepository) CountUsers(ctx context.Context) (int, error) {
	count, err := r.queries.CountUsers(ctx)
	if err != nil {
		return 0, err
	}

	return int(count), nil
}
