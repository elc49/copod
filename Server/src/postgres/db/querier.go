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
	AddToCart(ctx context.Context, arg AddToCartParams) (Cart, error)
	BuyRights(ctx context.Context, arg BuyRightsParams) (Payment, error)
	ClearTestCarts(ctx context.Context) error
	ClearTestFarms(ctx context.Context) error
	ClearTestMarkets(ctx context.Context) error
	ClearTestOrders(ctx context.Context) error
	ClearTestPosters(ctx context.Context) error
	ClearTestUsers(ctx context.Context) error
	CountUsers(ctx context.Context) (int64, error)
	CreateFarm(ctx context.Context, arg CreateFarmParams) (Farm, error)
	CreateFarmMarket(ctx context.Context, arg CreateFarmMarketParams) (Market, error)
	CreateOrder(ctx context.Context, arg CreateOrderParams) (Order, error)
	CreatePost(ctx context.Context, arg CreatePostParams) (Post, error)
	CreateUserByPhone(ctx context.Context, arg CreateUserByPhoneParams) (User, error)
	DeleteCartItem(ctx context.Context, marketID uuid.UUID) error
	GetCartItem(ctx context.Context, arg GetCartItemParams) (Cart, error)
	GetFarmByID(ctx context.Context, id uuid.UUID) (GetFarmByIDRow, error)
	GetFarmOwnerID(ctx context.Context, id uuid.UUID) (uuid.UUID, error)
	GetFarmsBelongingToUser(ctx context.Context, userID uuid.UUID) ([]GetFarmsBelongingToUserRow, error)
	GetLocalizedMarkets(ctx context.Context, arg GetLocalizedMarketsParams) ([]GetLocalizedMarketsRow, error)
	GetLocalizedPosters(ctx context.Context, arg GetLocalizedPostersParams) ([]GetLocalizedPostersRow, error)
	GetMarketByID(ctx context.Context, id uuid.UUID) (GetMarketByIDRow, error)
	GetMarketsBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]GetMarketsBelongingToFarmRow, error)
	GetOrderById(ctx context.Context, id uuid.UUID) (Order, error)
	GetOrdersBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]Order, error)
	GetOrdersBelongingToUser(ctx context.Context, customerID uuid.UUID) ([]Order, error)
	GetPaymentsBelongingToFarm(ctx context.Context, farmID uuid.NullUUID) ([]GetPaymentsBelongingToFarmRow, error)
	GetRightPurchasePaymentByReferenceID(ctx context.Context, referenceID sql.NullString) (Payment, error)
	GetUserByID(ctx context.Context, id uuid.UUID) (GetUserByIDRow, error)
	GetUserByPhone(ctx context.Context, phone string) (GetUserByPhoneRow, error)
	GetUserCartItems(ctx context.Context, userID uuid.UUID) ([]Cart, error)
	GetUserOrdersCount(ctx context.Context, customerID uuid.UUID) (int64, error)
	SetMarketStatus(ctx context.Context, arg SetMarketStatusParams) (Market, error)
	SetUserFarmingRights(ctx context.Context, arg SetUserFarmingRightsParams) (User, error)
	SetUserPosterRights(ctx context.Context, arg SetUserPosterRightsParams) (User, error)
	UpdateCartVolume(ctx context.Context, arg UpdateCartVolumeParams) (Cart, error)
	UpdateFarmDetails(ctx context.Context, arg UpdateFarmDetailsParams) (Farm, error)
	UpdateMarketVolume(ctx context.Context, arg UpdateMarketVolumeParams) (Market, error)
	UpdateOrderStatus(ctx context.Context, arg UpdateOrderStatusParams) (Order, error)
	UpdatePaystackPaymentStatus(ctx context.Context, arg UpdatePaystackPaymentStatusParams) (Payment, error)
}

var _ Querier = (*Queries)(nil)
