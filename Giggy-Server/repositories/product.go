package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type ProductRepository struct {
	queries *db.Queries
}

func (r *ProductRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *ProductRepository) GetProductsBelongingToStore(ctx context.Context, id uuid.UUID) ([]*model.Product, error) {
	var products []*model.Product
	ps, err := r.queries.GetProductsBelongingToStore(ctx, id)
	if err != nil {
		return nil, err
	}

	for _, item := range ps {
		product := &model.Product{
			ID:           item.ID,
			Name:         item.Name,
			Image:        item.Image,
			Volume:       int(item.Volume),
			PricePerUnit: int(item.PricePerUnit),
			CreatedAt:    item.CreatedAt,
			UpdatedAt:    item.UpdatedAt,
		}

		products = append(products, product)
	}

	return products, nil
}

func (r *ProductRepository) GetProductByID(ctx context.Context, id uuid.UUID) (*model.Product, error) {
	p, err := r.queries.GetProductByID(ctx, id)
	if err != nil && err == sql.ErrNoRows {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	return &model.Product{
		ID:           p.ID,
		Name:         p.Name,
		Image:        p.Image,
		Volume:       int(p.Volume),
		PricePerUnit: int(p.PricePerUnit),
		CreatedAt:    p.CreatedAt,
		UpdatedAt:    p.UpdatedAt,
	}, nil
}
