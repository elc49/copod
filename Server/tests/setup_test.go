package tests

import (
	"time"

	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/jwt"
	"github.com/elc49/copod/Server/src/postgres"
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
			Secret:  "LHE44aRVr3Fa5zlJmwZvcpyJCjtP75z2i7dvr5ASVzLUBNksecmtUYt4HQuKI2YxcNanu3oyNUMfM2oVS7Ke6iw0eNG48qvGui0MWaLEWUOKQfs7gRDEHgALpngfUbOqPv8KusLoPqhetlF5uhSHk8EUQH7vAYiJRTBkoasDDRePeKxbFWAMiaMKIc6dLiFxgkqCKJBL9FgDYHmXYobhMnoH9ti0rmseKbt3Ikq09C296K5rTs9GpWlphBq4qbWG",
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
