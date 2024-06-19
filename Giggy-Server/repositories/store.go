package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type StoreRepository struct {
	queries *db.Queries
}

func (r *StoreRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *StoreRepository) CreateStore(ctx context.Context, args db.CreateStoreParams) (*model.Store, error) {
	store, err := r.queries.CreateStore(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Store{
		ID: store.ID,
	}, nil
}

func (r *StoreRepository) GetStoresBelongingToUser(ctx context.Context, id uuid.UUID) ([]*model.Store, error) {
	var stores []*model.Store
	s, err := r.queries.GetStoresBelongingToUser(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range s {
		store := &model.Store{
			ID:        item.ID,
			Name:      item.Name,
			Thumbnail: item.Thumbnail,
		}
		stores = append(stores, store)
	}

	return stores, nil
}

func (r *StoreRepository) GetStoreByID(ctx context.Context, id uuid.UUID) (*model.Store, error) {
	store, err := r.queries.GetStoreByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Store{
		ID:        store.ID,
		Name:      store.Name,
		Thumbnail: store.Thumbnail,
	}, nil
}
