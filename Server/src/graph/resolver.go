package graph

import (
	"github.com/elc49/copod/Server/src/cache"
	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/redis/go-redis/v9"
)

// This file will not be regenerated automatically.
//
// It serves as dependency injection for your app, add any dependencies you require here.

type Resolver struct {
	farmController     controllers.FarmController
	signinController   controllers.SigninController
	marketController   controllers.MarketController
	orderController    controllers.OrderController
	paymentController  controllers.PaymentController
	paystackController controllers.PaystackController
	cartController     controllers.CartController
	reviewController   controllers.ReviewsController
	redis              *redis.Client
}

func New(store postgres.Store, signinController controllers.SigninController) Config {
	farmController := controllers.FarmController{}
	farmController.Init(store)
	marketController := controllers.MarketController{}
	marketController.Init(store)
	orderController := controllers.OrderController{}
	orderController.Init(store)
	paymentController := controllers.PaymentController{}
	paymentController.Init(store)
	paystackController := controllers.PaystackController{}
	paystackController.Init(store)
	cartController := controllers.CartController{}
	cartController.Init(store)
	reviewController := controllers.ReviewsController{}
	reviewController.Init(store)

	resolver := &Resolver{
		farmController,
		signinController,
		marketController,
		orderController,
		paymentController,
		paystackController,
		cartController,
		reviewController,
		cache.GetCache().GetRedis(),
	}

	c := Config{Resolvers: resolver}

	return c
}
