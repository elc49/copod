package repositories

import (
	"context"
	"database/sql"
	"strconv"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/google/uuid"
)

type FarmRepository struct {
	store postgres.Store
}

func (r *FarmRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *FarmRepository) CreateFarm(ctx context.Context, args db.CreateFarmParams) (*model.Farm, error) {
	farm, err := r.store.StoreWriter.CreateFarm(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Farm{
		ID:          farm.ID,
		Name:        farm.Name,
		About:       &farm.About,
		DateStarted: farm.DateStarted,
		Thumbnail:   farm.Thumbnail,
	}, nil
}

func (r *FarmRepository) GetFarmsBelongingToUser(ctx context.Context, id uuid.UUID) ([]*model.Farm, error) {
	var farms []*model.Farm
	s, err := r.store.StoreReader.GetFarmsBelongingToUser(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range s {
		farm := &model.Farm{
			ID:          item.ID,
			Name:        item.Name,
			About:       &item.About,
			DateStarted: item.DateStarted,
			Thumbnail:   item.Thumbnail,
		}
		farms = append(farms, farm)
	}

	return farms, nil
}

func (r *FarmRepository) GetFarmByID(ctx context.Context, id uuid.UUID) (*model.Farm, error) {
	farm, err := r.store.StoreReader.GetFarmByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	rate, err := r.reviewFarm(ctx, farm.ID)
	if err != nil {
		return nil, err
	}

	return &model.Farm{
		ID:            farm.ID,
		Name:          farm.Name,
		About:         &farm.About,
		AddressString: farm.AddressString,
		DateStarted:   farm.DateStarted,
		Rating:        rate,
		Thumbnail:     farm.Thumbnail,
		CreatedAt:     farm.CreatedAt,
		UpdatedAt:     farm.UpdatedAt,
	}, nil
}

func (r *FarmRepository) UpdateFarmDetails(ctx context.Context, args db.UpdateFarmDetailsParams) (*model.Farm, error) {
	f, err := r.store.StoreWriter.UpdateFarmDetails(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Farm{
		ID:        f.ID,
		About:     &f.About,
		Thumbnail: f.Thumbnail,
	}, nil
}

func (r *FarmRepository) reviewFarm(ctx context.Context, farmID uuid.UUID) (float64, error) {
	points, err := r.store.StoreReader.FarmRatingPoints(ctx, uuid.NullUUID{UUID: farmID, Valid: true})
	if err != nil && err == sql.ErrNoRows {
		return 0.0, nil
	} else if err != nil {
		return 0.0, err
	}

	reviewers, err := r.store.StoreReader.CountFarmReviewers(ctx, uuid.NullUUID{UUID: farmID, Valid: true})
	if err != nil && err == sql.ErrNoRows {
		return 0.0, nil
	} else if err != nil {
		return 0.0, err
	}

	rating, err := strconv.ParseFloat(strconv.FormatInt(int64(points)/reviewers, 10), 64)
	if err != nil {
		return 0.0, err
	}

	return rating, nil
}
