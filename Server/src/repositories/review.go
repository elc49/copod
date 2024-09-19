package repositories

import (
	"context"
	"database/sql"
	"strconv"

	"github.com/elc49/copod/Server/src/postgres"
	"github.com/google/uuid"
)

type ReviewsRepository struct {
	store postgres.Store
}

func (r *ReviewsRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *ReviewsRepository) FarmRating(ctx context.Context, farmID uuid.UUID) (*float64, error) {
	rC, err := r.store.StoreReader.FarmRating(ctx, uuid.NullUUID{UUID: farmID, Valid: true})
	if err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	f, err := strconv.ParseFloat(strconv.FormatInt(rC, 10), 64)
	if err != nil {
		return nil, err
	}

	return &f, nil
}

func (r *ReviewsRepository) FarmReviewers(ctx context.Context, farmID uuid.UUID) (*int, error) {
	count := 0
	c, err := r.store.StoreReader.FarmReviewers(ctx, uuid.NullUUID{UUID: farmID, Valid: true})
	if err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}
	count = int(c)

	return &count, nil
}
