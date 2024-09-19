package controllers

import (
	"context"

	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/repositories"
	"github.com/google/uuid"
)

type ReviewsController struct {
	r *repositories.ReviewsRepository
}

func (c *ReviewsController) Init(store postgres.Store) {
	c.r = &repositories.ReviewsRepository{}
	c.r.Init(store)
}

func (c *ReviewsController) FarmRating(ctx context.Context, farmID uuid.UUID) (float64, error) {
	count, err := c.r.FarmRating(ctx, farmID)
	if count == nil && err == nil {
		return 0.0, nil
	} else if err != nil {
		return 0.0, nil
	}

	return *count, nil
}

func (c *ReviewsController) FarmReviewers(ctx context.Context, farmID uuid.UUID) (int, error) {
	count, err := c.r.FarmReviewers(ctx, farmID)
	if count == nil && err == nil {
		return 0, nil
	} else if err != nil {
		return 0, err
	}

	return *count, nil
}
