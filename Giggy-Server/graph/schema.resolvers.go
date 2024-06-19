package graph

// This file will be automatically regenerated based on the schema, any resolver implementations
// will be copied through when generating and any unknown code will be moved to the end.
// Code generated by github.com/99designs/gqlgen version v0.17.47

import (
	"context"
	"fmt"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

// CreatePost is the resolver for the createPost field.
func (r *mutationResolver) CreatePost(ctx context.Context, input model.NewPostInput) (*model.Post, error) {
	userId := StringToUUID(ctx.Value("userId").(string))
	args := db.CreatePostParams{
		Text:     input.Text,
		Image:    input.Image,
		Tags:     input.Tags,
		UserID:   userId,
		Location: fmt.Sprintf("SRID=4326;POINT(%.8f %.8f)", input.Location.Lng, input.Location.Lat),
	}
	return r.postController.CreatePost(ctx, args)
}

// CreateStore is the resolver for the createStore field.
func (r *mutationResolver) CreateStore(ctx context.Context, input model.NewStoreInput) (*model.Store, error) {
	userId := StringToUUID(ctx.Value("userId").(string))
	args := db.CreateStoreParams{
		Name:      input.Name,
		Thumbnail: input.Thumbnail,
		UserID:    userId,
	}

	return r.storeController.CreateStore(ctx, args)
}

// User is the resolver for the user field.
func (r *postResolver) User(ctx context.Context, obj *model.Post) (*model.User, error) {
	return nil, nil
}

// Timeline is the resolver for the timeline field.
func (r *queryResolver) Timeline(ctx context.Context) ([]*model.Post, error) {
	return make([]*model.Post, 0), nil
}

// GetStoresBelongingToUser is the resolver for the getStoresBelongingToUser field.
func (r *queryResolver) GetStoresBelongingToUser(ctx context.Context) ([]*model.Store, error) {
	userId := StringToUUID(ctx.Value("userId").(string))
	return r.storeController.GetStoresBelongingToUser(ctx, userId)
}

// GetUser is the resolver for the getUser field.
func (r *queryResolver) GetUser(ctx context.Context) (*model.User, error) {
	userId := StringToUUID(ctx.Value("userId").(string))
	return r.signinController.GetUserByID(ctx, userId)
}

// GetStoreByID is the resolver for the getStoreById field.
func (r *queryResolver) GetStoreByID(ctx context.Context, id uuid.UUID) (*model.Store, error) {
	return r.storeController.GetStoreByID(ctx, id)
}

// Mutation returns MutationResolver implementation.
func (r *Resolver) Mutation() MutationResolver { return &mutationResolver{r} }

// Post returns PostResolver implementation.
func (r *Resolver) Post() PostResolver { return &postResolver{r} }

// Query returns QueryResolver implementation.
func (r *Resolver) Query() QueryResolver { return &queryResolver{r} }

type mutationResolver struct{ *Resolver }
type postResolver struct{ *Resolver }
type queryResolver struct{ *Resolver }
