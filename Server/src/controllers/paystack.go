package controllers

import (
	"context"
	"database/sql"
	"fmt"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/paystack"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/elc49/vuno/Server/src/util"
	"github.com/google/uuid"
)

type SubscriptionController struct {
	paystack paystack.Paystack
	db       *db.Queries
}

func (c *SubscriptionController) Init(db *db.Queries) {
	c.paystack = paystack.GetPaystackService()
	c.db = db
}

func (c *SubscriptionController) ChargeMpesaPhone(ctx context.Context, userId uuid.UUID, input model.PayWithMpesaInput) (*model.ChargeMpesaPhoneRes, error) {
	args := model.ChargeMpesaPhoneInput{
		Currency: input.Currency,
		Email:    fmt.Sprintf("%s@giggy.app", util.RandomStringByLength(5)),
		UserID:   userId,
		Reason:   input.Reason,
		Provider: struct {
			Phone    string `json:"phone"`
			Provider string `json:"provider"`
		}{
			Phone: input.Phone,
		},
	}

	switch input.Reason {
	case "poster_rights":
		args.Amount = config.Configuration.Fees.PosterRights * 100
	case "farming_rights":
		args.Amount = config.Configuration.Fees.FarmingRights * 100
	default:
	}

	return c.paystack.ChargeMpesaPhone(ctx, args)
}

func (c *SubscriptionController) VerifyTransactionByReferenceID(ctx context.Context, referenceId string) (*model.PaystackPaymentUpdate, error) {
	payment, err := c.db.GetRightPurchasePaymentByReferenceID(ctx, sql.NullString{String: referenceId, Valid: true})
	if err != nil {
		return nil, err
	}

	res, err := c.paystack.VerifyTransactionByReferenceID(ctx, referenceId)
	if err != nil {
		return nil, err
	}

	return &model.PaystackPaymentUpdate{Status: res.Data.Status, SessionID: payment.UserID}, nil
}
