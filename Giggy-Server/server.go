package main

import (
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/99designs/gqlgen/graphql/handler"
	"github.com/99designs/gqlgen/graphql/playground"
	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph"
	"github.com/elc49/giggy-monorepo/Giggy-Server/handlers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/ip"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

func main() {
	config.New()
	logger.New()
	ip.NewIpinfoClient()

	r := chi.NewRouter()
	r.Use(middleware.RealIP)
	r.Use(middleware.Recoverer)
	r.Use(middleware.Logger)
	r.Use(middleware.Timeout(60 * time.Second))
	srv := handler.NewDefaultServer(graph.NewExecutableSchema(graph.New()))

	r.Handle("/", playground.Handler("GraphQL playground", "/query"))
	r.Handle("/query", srv)
	r.Handle("/ip", handlers.Ip())

	s := &http.Server{
		Addr:    fmt.Sprintf("0.0.0.0:%s", config.Configuration.Server.Port),
		Handler: r,
	}
	log.Fatalln(s.ListenAndServe())
}
