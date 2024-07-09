// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0

package db

import (
	"context"
	"database/sql"

	"github.com/google/uuid"
)

type Querier interface {
	BuyRights(ctx context.Context, arg BuyRightsParams) (Payment, error)
	ClearTestFarms(ctx context.Context) error
	ClearTestMarkets(ctx context.Context) error
	ClearTestOrders(ctx context.Context) error
	ClearTestPosters(ctx context.Context) error
	ClearTestUsers(ctx context.Context) error
	CountUsers(ctx context.Context) (int64, error)
	CreateFarm(ctx context.Context, arg CreateFarmParams) (Farm, error)
	CreateFarmMarket(ctx context.Context, arg CreateFarmMarketParams) (Market, error)
	CreatePost(ctx context.Context, arg CreatePostParams) (Post, error)
	CreateUserByPhone(ctx context.Context, arg CreateUserByPhoneParams) (User, error)
	GetFarmByID(ctx context.Context, id uuid.UUID) (GetFarmByIDRow, error)
	GetFarmsBelongingToUser(ctx context.Context, userID uuid.UUID) ([]GetFarmsBelongingToUserRow, error)
	GetLocalizedMarkets(ctx context.Context, arg GetLocalizedMarketsParams) ([]GetLocalizedMarketsRow, error)
	GetLocalizedPosters(ctx context.Context, arg GetLocalizedPostersParams) ([]GetLocalizedPostersRow, error)
	GetMarketByID(ctx context.Context, id uuid.UUID) (GetMarketByIDRow, error)
	GetMarketsBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]GetMarketsBelongingToFarmRow, error)
	GetOrderById(ctx context.Context, id uuid.UUID) (GetOrderByIdRow, error)
	GetOrdersBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]Order, error)
	GetPaymentsBelongingToFarm(ctx context.Context, farmID uuid.NullUUID) ([]GetPaymentsBelongingToFarmRow, error)
	GetRightPurchasePaymentByReferenceID(ctx context.Context, referenceID sql.NullString) (Payment, error)
	GetUserByID(ctx context.Context, id uuid.UUID) (GetUserByIDRow, error)
	GetUserByPhone(ctx context.Context, phone string) (GetUserByPhoneRow, error)
	SetFarmingRights(ctx context.Context, arg SetFarmingRightsParams) (User, error)
	SetPosterRights(ctx context.Context, arg SetPosterRightsParams) (User, error)
	UpdateRightsPurchaseStatus(ctx context.Context, arg UpdateRightsPurchaseStatusParams) (Payment, error)
}

var _ Querier = (*Queries)(nil)
