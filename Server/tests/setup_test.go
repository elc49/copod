package tests

import (
	"time"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/controllers"
	"github.com/elc49/vuno/Server/src/jwt"
	"github.com/elc49/vuno/Server/src/postgres"
)

var (
	store  postgres.Store
	phone  = "254791215745"
	avatar = "https://avatar.jpg"
)

func init() {
	dbOption := config.Rdbms{
		Postgres: config.Postgres{
			DbUser:     "postgres",
			Driver:     "postgres",
			Port:       "5432",
			WriterHost: "localhost",
			ReaderHost: "localhost",
			DbPass:     "demo1234",
			DbName:     "test",
			Migrate:    true,
			Migration:  "file://../src/postgres/migrations",
		},
	}
	store = postgres.Store{
		StoreReader: postgres.InitReader(dbOption),
		StoreWriter: postgres.InitWriter(dbOption),
	}
	jwt.New(
		config.Jwt{
			Secret:  "raaF5qddWsCz3h1WruzgUtz1MmjgSCQI",
			Expires: time.Duration(time.Minute),
		},
	)
}

func signinController() controllers.SigninController {
	c := controllers.SigninController{}
	c.Init(store)
	return c
}

func userController() controllers.UserController {
	c := controllers.UserController{}
	c.Init(store)
	return c
}

func farmController() controllers.FarmController {
	c := controllers.FarmController{}
	c.Init(store)
	return c
}

func marketController() controllers.MarketController {
	c := controllers.MarketController{}
	c.Init(store)
	return c
}

func postController() controllers.PostController {
	c := controllers.PostController{}
	c.Init(store)
	return c
}

func cartController() controllers.CartController {
	c := controllers.CartController{}
	c.Init(store)
	return c
}

func orderController() controllers.OrderController {
	c := controllers.OrderController{}
	c.Init(store)
	return c
}
