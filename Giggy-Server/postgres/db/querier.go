// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0

package db

import (
	"context"

	"github.com/google/uuid"
)

type Querier interface {
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
	GetMarketByID(ctx context.Context, id uuid.UUID) (GetMarketByIDRow, error)
	GetMarketsBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]GetMarketsBelongingToFarmRow, error)
	GetNearbyMarkets(ctx context.Context, arg GetNearbyMarketsParams) ([]GetNearbyMarketsRow, error)
	GetOrderById(ctx context.Context, id uuid.UUID) (GetOrderByIdRow, error)
	GetOrdersBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]Order, error)
	GetPaymentsBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]GetPaymentsBelongingToFarmRow, error)
	GetUserByID(ctx context.Context, id uuid.UUID) (GetUserByIDRow, error)
	GetUserByPhone(ctx context.Context, phone string) (GetUserByPhoneRow, error)
	SetFarmingRights(ctx context.Context, arg SetFarmingRightsParams) (User, error)
	SetPosterRights(ctx context.Context, arg SetPosterRightsParams) (User, error)
}

var _ Querier = (*Queries)(nil)
