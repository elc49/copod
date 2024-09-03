package repositories

import (
	"context"
	"database/sql"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/google/uuid"
)

type CartRepository struct {
	store postgres.Store
}

func (r *CartRepository) Init(store postgres.Store) {
	r.store = store
}

func (r *CartRepository) AddToCart(ctx context.Context, args db.AddToCartParams) (*model.Cart, error) {
	existingArgs := db.GetCartItemParams{
		MarketID: args.MarketID,
		FarmID:   args.FarmID,
		UserID:   args.UserID,
	}
	existing, err := r.GetCartItem(ctx, existingArgs)
	if err != nil && err == sql.ErrNoRows {
		// Not in cart
		cart, err := r.store.StoreWriter.AddToCart(ctx, args)
		if err != nil {
			return nil, err
		}

		return &model.Cart{
			ID:        cart.ID,
			Volume:    int(cart.Volume),
			MarketID:  cart.MarketID,
			UserID:    cart.UserID,
			FarmID:    cart.FarmID,
			CreatedAt: cart.CreatedAt,
			UpdatedAt: cart.UpdatedAt,
		}, nil
	}

	// Update cart item new volume
	updateArgs := db.UpdateCartVolumeParams{
		ID:     existing.ID,
		Volume: args.Volume,
	}
	update, err := r.UpdateCartVolume(ctx, updateArgs)
	if err != nil {
		return nil, err
	}

	return update, nil
}

func (r *CartRepository) GetCartItem(ctx context.Context, args db.GetCartItemParams) (*model.Cart, error) {
	cart, err := r.store.StoreReader.GetCartItem(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Cart{
		ID:        cart.ID,
		Volume:    int(cart.Volume),
		MarketID:  cart.MarketID,
		UserID:    cart.UserID,
		FarmID:    cart.FarmID,
		CreatedAt: cart.CreatedAt,
		UpdatedAt: cart.UpdatedAt,
	}, nil
}

func (r *CartRepository) UpdateCartVolume(ctx context.Context, args db.UpdateCartVolumeParams) (*model.Cart, error) {
	cart, err := r.store.StoreWriter.UpdateCartVolume(ctx, args)
	if err != nil {
		return nil, err
	}

	return &model.Cart{
		ID:        cart.ID,
		Volume:    int(cart.Volume),
		MarketID:  cart.MarketID,
		UserID:    cart.UserID,
		FarmID:    cart.FarmID,
		CreatedAt: cart.CreatedAt,
		UpdatedAt: cart.UpdatedAt,
	}, nil
}

func (r *CartRepository) GetUserCartItems(ctx context.Context, userID uuid.UUID) ([]*model.Cart, error) {
	var carts []*model.Cart
	c, err := r.store.StoreReader.GetUserCartItems(ctx, userID)
	if err != nil {
		return nil, err
	}

	for _, item := range c {
		cart := &model.Cart{
			ID:        item.ID,
			Volume:    int(item.Volume),
			MarketID:  item.MarketID,
			FarmID:    item.FarmID,
			UserID:    item.UserID,
			CreatedAt: item.CreatedAt,
			UpdatedAt: item.UpdatedAt,
		}

		carts = append(carts, cart)
	}

	return carts, nil
}

func (r *CartRepository) DeleteCartItem(ctx context.Context, marketID uuid.UUID) (bool, error) {
	err := r.store.StoreWriter.DeleteCartItem(ctx, marketID)
	if err != nil {
		return false, err
	}

	return true, nil
}
