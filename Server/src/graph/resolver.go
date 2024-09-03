package graph

import (
	"github.com/elc49/copod/Server/src/cache"
	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/redis/go-redis/v9"
)

// This file will not be regenerated automatically.
//
// It serves as dependency injection for your app, add any dependencies you require here.

var log = logger.GetLogger()

type Resolver struct {
	postController         controllers.PostController
	farmController         controllers.FarmController
	signinController       controllers.SigninController
	marketController       controllers.MarketController
	orderController        controllers.OrderController
	paymentController      controllers.PaymentController
	subscriptionController controllers.PaystackController
	cartController         controllers.CartController
	redis                  *redis.Client
}

func New(store postgres.Store, signinController controllers.SigninController) Config {
	postController := controllers.PostController{}
	postController.Init(store)
	farmController := controllers.FarmController{}
	farmController.Init(store)
	marketController := controllers.MarketController{}
	marketController.Init(store)
	orderController := controllers.OrderController{}
	orderController.Init(store)
	paymentController := controllers.PaymentController{}
	paymentController.Init(store)
	subscriptionController := controllers.PaystackController{}
	subscriptionController.Init(store)
	cartController := controllers.CartController{}
	cartController.Init(store)

	resolver := &Resolver{
		postController,
		farmController,
		signinController,
		marketController,
		orderController,
		paymentController,
		subscriptionController,
		cartController,
		cache.GetCache().GetRedis(),
	}

	c := Config{Resolvers: resolver}

	return c
}
