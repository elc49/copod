package graph

// This file will be automatically regenerated based on the schema, any resolver implementations
// will be copied through when generating and any unknown code will be moved to the end.
// Code generated by github.com/99designs/gqlgen version v0.17.47

import (
	"context"
	"fmt"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/util"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

// Market is the resolver for the market field.
func (r *cartResolver) Market(ctx context.Context, obj *model.Cart) (*model.Market, error) {
	return r.marketController.GetMarketByID(ctx, obj.MarketID)
}

// CreatePost is the resolver for the createPost field.
func (r *mutationResolver) CreatePost(ctx context.Context, input model.NewPostInput) (*model.Post, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	args := db.CreatePostParams{
		Text:     input.Text,
		Image:    input.Image,
		Tags:     input.Tags,
		UserID:   userId,
		Location: fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", input.Location.Lng, input.Location.Lat),
	}
	return r.postController.CreatePost(ctx, args)
}

// CreateFarm is the resolver for the createFarm field.
func (r *mutationResolver) CreateFarm(ctx context.Context, input model.NewFarmInput) (*model.Farm, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	args := db.CreateFarmParams{
		Name:      input.Name,
		Thumbnail: input.Thumbnail,
		UserID:    userId,
	}

	return r.farmController.CreateFarm(ctx, args)
}

// CreateFarmMarket is the resolver for the createFarmMarket field.
func (r *mutationResolver) CreateFarmMarket(ctx context.Context, input model.NewFarmMarketInput) (*model.Market, error) {
	args := db.CreateFarmMarketParams{
		Product:      input.Product,
		Image:        input.Image,
		Volume:       int32(input.Volume),
		Tag:          input.Tag,
		Location:     fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", input.Location.Lng, input.Location.Lat),
		FarmID:       input.FarmID,
		Unit:         input.Unit,
		PricePerUnit: int32(input.PricePerUnit),
	}

	return r.marketController.CreateFarmMarket(ctx, args)
}

// PayWithMpesa is the resolver for the payWithMpesa field.
func (r *mutationResolver) PayWithMpesa(ctx context.Context, input model.PayWithMpesaInput) (*model.PayWithMpesa, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	res, err := r.subscriptionController.ChargeMpesaPhone(ctx, userId, input)
	if err != nil {
		return nil, err
	}

	return &model.PayWithMpesa{ReferenceID: res.Data.Reference}, nil
}

// AddToCart is the resolver for the addToCart field.
func (r *mutationResolver) AddToCart(ctx context.Context, input model.AddToCartInput) (*model.Cart, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	args := db.AddToCartParams{
		Volume:   int32(input.Volume),
		UserID:   userId,
		MarketID: input.MarketID,
		FarmID:   input.FarmID,
	}
	return r.cartController.AddToCart(ctx, args)
}

// Market is the resolver for the market field.
func (r *orderResolver) Market(ctx context.Context, obj *model.Order) (*model.Market, error) {
	return r.marketController.GetMarketByID(ctx, obj.MarketID)
}

// Customer is the resolver for the customer field.
func (r *orderResolver) Customer(ctx context.Context, obj *model.Order) (*model.User, error) {
	return r.signinController.GetUserByID(ctx, obj.CustomerID)
}

// User is the resolver for the user field.
func (r *postResolver) User(ctx context.Context, obj *model.Post) (*model.User, error) {
	return r.signinController.GetUserByID(ctx, obj.UserID)
}

// GetLocalizedPosters is the resolver for the getLocalizedPosters field.
func (r *queryResolver) GetLocalizedPosters(ctx context.Context, radius model.GpsInput) ([]*model.Post, error) {
	args := db.GetLocalizedPostersParams{
		Point:  fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", radius.Lng, radius.Lat),
		Radius: 5000,
	}
	return r.postController.GetLocalizedPosters(ctx, args)
}

// GetFarmsBelongingToUser is the resolver for the getFarmsBelongingToUser field.
func (r *queryResolver) GetFarmsBelongingToUser(ctx context.Context) ([]*model.Farm, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	return r.farmController.GetFarmsBelongingToUser(ctx, userId)
}

// GetUser is the resolver for the getUser field.
func (r *queryResolver) GetUser(ctx context.Context) (*model.User, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	return r.signinController.GetUserByID(ctx, userId)
}

// GetLocalizedMarkets is the resolver for the getLocalizedMarkets field.
func (r *queryResolver) GetLocalizedMarkets(ctx context.Context, radius model.GpsInput) ([]*model.Market, error) {
	args := db.GetLocalizedMarketsParams{
		Point:  fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", radius.Lng, radius.Lat),
		Radius: 2000000,
	}
	return r.marketController.GetLocalizedMarkets(ctx, args)
}

// GetFarmByID is the resolver for the getFarmById field.
func (r *queryResolver) GetFarmByID(ctx context.Context, id uuid.UUID) (*model.Farm, error) {
	return r.farmController.GetFarmByID(ctx, id)
}

// GetFarmMarkets is the resolver for the getFarmMarkets field.
func (r *queryResolver) GetFarmMarkets(ctx context.Context, id uuid.UUID) ([]*model.Market, error) {
	return r.marketController.GetMarketsBelongingToFarm(ctx, id)
}

// GetFarmOrders is the resolver for the getFarmOrders field.
func (r *queryResolver) GetFarmOrders(ctx context.Context, id uuid.UUID) ([]*model.Order, error) {
	return r.orderController.GetOrdersBelongingToFarm(ctx, id)
}

// GetFarmPayments is the resolver for the getFarmPayments field.
func (r *queryResolver) GetFarmPayments(ctx context.Context, id uuid.UUID) ([]*model.Payment, error) {
	return r.paymentController.GetPaymentsBelongingToFarm(ctx, id)
}

// GetPaystackPaymentVerification is the resolver for the getPaystackPaymentVerification field.
func (r *queryResolver) GetPaystackPaymentVerification(ctx context.Context, referenceID string) (*model.PaystackPaymentVerificationStatus, error) {
	return r.subscriptionController.VerifyTransactionByReferenceID(ctx, referenceID)
}

// GetUserCartItems is the resolver for the getUserCartItems field.
func (r *queryResolver) GetUserCartItems(ctx context.Context) ([]*model.Cart, error) {
	userId := util.StringToUUID(ctx.Value("userId").(string))
	return r.cartController.GetUserCartItems(ctx, userId)
}

// Cart returns CartResolver implementation.
func (r *Resolver) Cart() CartResolver { return &cartResolver{r} }

// Mutation returns MutationResolver implementation.
func (r *Resolver) Mutation() MutationResolver { return &mutationResolver{r} }

// Order returns OrderResolver implementation.
func (r *Resolver) Order() OrderResolver { return &orderResolver{r} }

// Post returns PostResolver implementation.
func (r *Resolver) Post() PostResolver { return &postResolver{r} }

// Query returns QueryResolver implementation.
func (r *Resolver) Query() QueryResolver { return &queryResolver{r} }

type cartResolver struct{ *Resolver }
type mutationResolver struct{ *Resolver }
type orderResolver struct{ *Resolver }
type postResolver struct{ *Resolver }
type queryResolver struct{ *Resolver }
