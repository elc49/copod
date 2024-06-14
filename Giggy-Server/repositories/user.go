package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
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

func (r *UserRepository) GetUserByID(ctx context.Context, id uuid.UUID) (*model.User, error) {
	user, err := r.queries.GetUserByID(ctx, id)
	if err != nil {
		return nil, err
	}

	return &model.User{
		ID: user.ID,
	}, nil
}
