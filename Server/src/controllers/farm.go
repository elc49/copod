package controllers

import (
	"context"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/elc49/vuno/Server/src/repositories"
	"github.com/google/uuid"
)

type FarmController struct {
	r *repositories.FarmRepository
}

func (r *FarmController) Init(store postgres.Store) {
	r.r = &repositories.FarmRepository{}
	r.r.Init(store)
}

func (r *FarmController) CreateFarm(ctx context.Context, args db.CreateFarmParams) (*model.Farm, error) {
	return r.r.CreateFarm(ctx, args)
}

func (r *FarmController) GetFarmsBelongingToUser(ctx context.Context, id uuid.UUID) ([]*model.Farm, error) {
	return r.r.GetFarmsBelongingToUser(ctx, id)
}

func (r *FarmController) GetFarmByID(ctx context.Context, id uuid.UUID) (*model.Farm, error) {
	return r.r.GetFarmByID(ctx, id)
}
