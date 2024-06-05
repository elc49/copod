package tests

import (
	"fmt"

	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
)

var (
	queries *db.Queries
)

func init() {
	fmt.Println("started")
	queries = postgres.Init(postgres.ConnectOption{
		Uri:           "postgres://postgres:demo1234@localhost:5432/test?sslmode=disable",
		Driver:        "postgres",
		Migrate:       true,
		MigrationFile: "file://../postgres/migrations",
	})
}

func mobileSigninController() controllers.MobileSigninController {
	c := controllers.MobileSigninController{}
	c.Init(queries)
	return c
}

func sessionController() controllers.SessionController {
	c := controllers.SessionController{}
	c.Init(queries)
	return c
}
