package main

import (
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/99designs/gqlgen/graphql/handler"
	"github.com/99designs/gqlgen/graphql/playground"
	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph"
	"github.com/elc49/giggy-monorepo/Giggy-Server/handlers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/gcloud"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/ip"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/jwt"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	giggyMiddleware "github.com/elc49/giggy-monorepo/Giggy-Server/middleware"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

func main() {
	config.New()
	logger.New()
	jwt.New(config.Jwt{
		Secret:  config.Configuration.Jwt.Secret,
		Expires: config.Configuration.Jwt.Expires,
	})
	ip.NewIpinfoClient()
	gcloud.New()
	queries := postgres.Init(config.Rdbms{
		Driver:        config.Configuration.Rdbms.Driver,
		Uri:           config.Configuration.Rdbms.Uri,
		Migrate:       config.Configuration.Rdbms.Migrate,
		MigrationFile: config.Configuration.Rdbms.MigrationFile,
	})
	signinController := controllers.SigninController{}
	signinController.Init(queries)
	userController := controllers.UserController{}
	userController.Init(queries)

	r := chi.NewRouter()
	r.Use(middleware.RealIP)
	r.Use(middleware.Recoverer)
	r.Use(middleware.Logger)
	r.Use(middleware.Timeout(60 * time.Second))
	srv := handler.NewDefaultServer(graph.NewExecutableSchema(graph.New(queries)))

	r.Handle("/", playground.Handler("GraphQL playground", "/api/graphql"))
	r.Route("/api", func(r chi.Router) {
		r.With(giggyMiddleware.Auth).Handle("/graphql", srv)
		r.Handle("/ip", handlers.Ip())
		r.Handle("/mobile/signin", handlers.MobileSignin(signinController))
		r.Handle("/post/uploads", handlers.ImageUploader())
		r.Handle("/refresh/token", handlers.RefreshToken(userController))
		r.Handle("/store/uploads", handlers.ImageUploader())
	})

	s := &http.Server{
		Addr:    fmt.Sprintf("0.0.0.0:%s", config.Configuration.Server.Port),
		Handler: r,
	}
	log.Fatalln(s.ListenAndServe())
}
