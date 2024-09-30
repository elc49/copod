package repositories

import (
	"context"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/google/uuid"
	"github.com/sirupsen/logrus"
)

type PaymentRepository struct {
	store postgres.Store
	log   *logrus.Logger
}

func (r *PaymentRepository) Init(store postgres.Store) {
	r.store = store
	r.log = logger.GetLogger()
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

func (r *PaymentRepository) BuyRights(ctx context.Context, arg db.BuyRightsParams) (*model.Payment, error) {
	p, err := r.store.StoreWriter.BuyRights(ctx, arg)
	if err != nil {
		r.log.WithFields(logrus.Fields{"args": arg}).WithError(err).Errorf("repository: payment")
		return nil, err
	}

	return &model.Payment{
		ID:        p.ID,
		CreatedAt: p.CreatedAt,
		UpdatedAt: p.UpdatedAt,
	}, nil
}
