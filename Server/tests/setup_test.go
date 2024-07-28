package tests

import (
	"time"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/controllers"
	"github.com/elc49/vuno/Server/src/jwt"
	"github.com/elc49/vuno/Server/src/postgres"
	"github.com/elc49/vuno/Server/src/postgres/db"
)

var (
	queries *db.Queries
	phone   = "254791215745"
	avatar  = "https://avatar.jpg"
)

func init() {
	queries = postgres.Init(config.Rdbms{
		Uri:           "postgres://postgres:demo1234@localhost:5432/test?sslmode=disable",
		Driver:        "postgres",
		Migrate:       true,
		MigrationFile: "file://../src/postgres/migrations",
	})
	jwt.New(
		config.Jwt{
			Secret:  "raaF5qddWsCz3h1WruzgUtz1MmjgSCQI",
			Expires: time.Duration(time.Minute),
		},
	)
}

func signinController() controllers.SigninController {
	c := controllers.SigninController{}
	c.Init(queries)
	return c
}

func userController() controllers.UserController {
	c := controllers.UserController{}
	c.Init(queries)
	return c
}

func farmController() controllers.FarmController {
	c := controllers.FarmController{}
	c.Init(queries)
	return c
}

func marketController() controllers.MarketController {
	c := controllers.MarketController{}
	c.Init(queries)
	return c
}

func postController() controllers.PostController {
	c := controllers.PostController{}
	c.Init(queries)
	return c
}

func cartController() controllers.CartController {
	c := controllers.CartController{}
	c.Init(queries)
	return c
}

func orderController() controllers.OrderController {
	c := controllers.OrderController{}
	c.Init(queries)
	return c
}
