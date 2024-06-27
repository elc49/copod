package tests

import (
	"time"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/jwt"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
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
		MigrationFile: "file://../postgres/migrations",
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
