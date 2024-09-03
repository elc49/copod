package repositories

import (
	"context"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/google/uuid"
)

type PaymentRepository struct {
	store postgres.Store
}

func (r *PaymentRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *PaymentRepository) GetPaymentsBelongingToFarm(ctx context.Context, id uuid.UUID) ([]*model.Payment, error) {
	var payments []*model.Payment
	ps, err := r.store.StoreReader.GetPaymentsBelongingToFarm(ctx, uuid.NullUUID{UUID: id, Valid: true})
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
