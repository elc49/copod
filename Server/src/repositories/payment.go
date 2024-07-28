package repositories

import (
	"context"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/google/uuid"
)

type PaymentRepository struct {
	queries *db.Queries
}

func (r *PaymentRepository) Init(queries *db.Queries) {
	r.queries = queries
}

func (r *PaymentRepository) GetPaymentsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Payment, error) {
	var payments []*model.Payment
	ps, err := r.queries.GetPaymentsBelongingToFarm(ctx, uuid.NullUUID{UUID: id, Valid: true})
	if err != nil {
		return nil, err
	}

	for _, item := range ps {
		payment := &model.Payment{
			ID:       item.ID,
			Amount:   int(item.Amount),
			Customer: item.Customer,
			Status:   item.Status,
		}

		payments = append(payments, payment)
	}

	return payments, nil
}
