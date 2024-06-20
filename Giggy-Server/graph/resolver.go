package graph

import (
	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/google/uuid"
)

// This file will not be regenerated automatically.
//
// It serves as dependency injection for your app, add any dependencies you require here.

var log = logger.GetLogger()

type Resolver struct {
	postController    controllers.PostController
	storeController   controllers.StoreController
	signinController  controllers.SigninController
	productController controllers.ProductController
	orderController   controllers.OrderController
	paymentController controllers.PaymentController
}

func New(db *db.Queries, signinController controllers.SigninController) Config {
	postController := controllers.PostController{}
	postController.Init(db)
	storeController := controllers.StoreController{}
	storeController.Init(db)
	productController := controllers.ProductController{}
	productController.Init(db)
	orderController := controllers.OrderController{}
	orderController.Init(db)
	paymentController := controllers.PaymentController{}
	paymentController.Init(db)

	resolver := &Resolver{
		postController,
		storeController,
		signinController,
		productController,
		orderController,
		paymentController,
	}

	c := Config{Resolvers: resolver}

	return c
}

func StringToUUID(id string) uuid.UUID {
	uid, err := uuid.Parse(id)
	if err != nil {
		log.WithError(err).Error("resolver: StringToUUID()")
	}

	return uid
}
