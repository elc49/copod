package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type FarmRepository struct {
	queries *db.Queries
}

func (r *FarmRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *FarmRepository) CreateFarm(ctx context.Context, args db.CreateFarmParams) (*model.Farm, error) {
	farm, err := r.queries.CreateFarm(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Farm{
		ID:        farm.ID,
		Name:      farm.Name,
		Thumbnail: farm.Thumbnail,
	}, nil
}

func (r *FarmRepository) GetFarmsBelongingToUser(ctx context.Context, id uuid.UUID) ([]*model.Farm, error) {
	var farms []*model.Farm
	s, err := r.queries.GetFarmsBelongingToUser(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range s {
		farm := &model.Farm{
			ID:        item.ID,
			Name:      item.Name,
			Thumbnail: item.Thumbnail,
		}
		farms = append(farms, farm)
	}

	return farms, nil
}

func (r *FarmRepository) GetFarmByID(ctx context.Context, id uuid.UUID) (*model.Farm, error) {
	farm, err := r.queries.GetFarmByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Farm{
		ID:        farm.ID,
		Name:      farm.Name,
		Thumbnail: farm.Thumbnail,
	}, nil
}
