package controllers

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/elc49/giggy-monorepo/Giggy-Server/repositories"
	"github.com/google/uuid"
)

type ProductController struct {
	r *repositories.ProductRepository
}

func (c *ProductController) Init(queries *db.Queries) {
	c.r = &repositories.ProductRepository{}
	c.r.Init(queries)
}

func (c *ProductController) GetProductsBelongingToStore(ctx context.Context, id uuid.UUID) ([]*model.Product, error) {
	return c.r.GetProductsBelongingToStore(ctx, id)
}

func (c *ProductController) GetProductByID(ctx context.Context, id uuid.UUID) (*model.Product, error) {
	return c.r.GetProductByID(ctx, id)
}

func (c *ProductController) CreateStoreProduct(ctx context.Context, args db.CreateStoreProductParams) (*model.Product, error) {
	return c.r.CreateStoreProduct(ctx, args)
}
