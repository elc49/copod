package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type StoreController struct {
	r *repositories.StoreRepository
}

func (r *StoreController) Init(queries *db.Queries) {
	r.r = &repositories.StoreRepository{}
	r.r.Init(queries)
}

func (r *StoreController) CreateStore(ctx context.Context, args db.CreateStoreParams) (*model.Store, error) {
	return r.r.CreateStore(ctx, args)
}

func (r *StoreController) GetStoresBelongingToUser(ctx context.Context, id uuid.UUID) ([]*model.Store, error) {
	return r.r.GetStoresBelongingToUser(ctx, id)
}
