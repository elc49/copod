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
	store, err := r.queries.CreateFarm(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Farm{
		ID: store.ID,
	}, nil
}

func (r *FarmRepository) GetFarmsBelongingToUser(ctx context.Context, id uuid.UUID) ([]*model.Farm, error) {
	var stores []*model.Farm
	s, err := r.queries.GetFarmsBelongingToUser(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range s {
		store := &model.Farm{
			ID:        item.ID,
			Name:      item.Name,
			Thumbnail: item.Thumbnail,
		}
		stores = append(stores, store)
	}

	return stores, nil
}

func (r *FarmRepository) GetFarmByID(ctx context.Context, id uuid.UUID) (*model.Farm, error) {
	store, err := r.queries.GetFarmByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Farm{
		ID:        store.ID,
		Name:      store.Name,
		Thumbnail: store.Thumbnail,
	}, nil
}
