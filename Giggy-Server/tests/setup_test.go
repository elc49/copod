package tests

import (
	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

var (
	queries *db.Queries
	phone   = "254791215745"
)

func init() {
	queries = postgres.Init(postgres.ConnectOption{
		Uri:           "postgres://postgres:demo1234@localhost:5432/test?sslmode=disable",
		Driver:        "postgres",
		Migrate:       true,
		MigrationFile: "file://../postgres/migrations",
	})
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

func sessionController() controllers.SessionController {
	c := controllers.SessionController{}
	c.Init(queries)
	return c
}
