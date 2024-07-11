package repositories

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

type CartRepository struct {
	db *db.Queries
}

func (r *CartRepository) Init(db *db.Queries) {
	r.db = db
}

func (r *CartRepository) AddToCart(ctx context.Context, args db.AddToCartParams) (*model.Cart, error) {
	cart, err := r.db.AddToCart(ctx, args)
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

func (r *CartRepository) GetCartItem(ctx context.Context, args db.GetCartItemParams) (*model.Cart, error) {
	cart, err := r.db.GetCartItem(ctx, args)
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
	cart, err := r.db.UpdateCartVolume(ctx, args)
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
	c, err := r.db.GetUserCartItems(ctx, userID)
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
