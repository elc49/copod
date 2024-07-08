package server

import (
	"time"

	"github.com/99designs/gqlgen/graphql/handler"
	"github.com/99designs/gqlgen/graphql/handler/transport"
	"github.com/99designs/gqlgen/graphql/playground"
	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph"
	"github.com/elc49/giggy-monorepo/Giggy-Server/handlers"
	webhook "github.com/elc49/giggy-monorepo/Giggy-Server/handlers/webhook"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/cache"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/gcloud"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/ip"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/jwt"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/paystack"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	giggyMiddleware "github.com/elc49/giggy-monorepo/Giggy-Server/middleware"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres"
	"github.com/elc49/giggy-monorepo/Giggy-Server/postgres/db"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

type Server struct {
	Router *chi.Mux
	Db     *db.Queries
}

func New() *Server {
	s := &Server{}
	s.Router = chi.NewRouter()
	s.Config()
	s.Db = s.Database(config.Configuration.Rdbms)
	s.Services()
	return s
}

func (s *Server) Database(dbConfig config.Rdbms) *db.Queries {
	queries := postgres.Init(dbConfig)
	return queries
}

func (s *Server) Config() { config.New() }

func (s *Server) Services() {
	logger.New()
	cache.New()
	jwt.New(config.Jwt{
		Secret:  config.Configuration.Jwt.Secret,
		Expires: config.Configuration.Jwt.Expires,
	})
	ip.NewIpinfoClient()
	gcloud.New()
	paystack.New()
}

func (s *Server) MountHandlers() {
	// Data controllers
	signinController := controllers.SigninController{}
	signinController.Init(s.Db)
	userController := controllers.UserController{}
	userController.Init(s.Db)

	// GraphQL handler
	graphqlHandler := handler.NewDefaultServer(graph.NewExecutableSchema(graph.New(s.Db, signinController)))
	graphqlHandler.AddTransport(&transport.Websocket{})

	// Middlewares
	s.Router.Use(middleware.Heartbeat("/ping"))
	s.Router.Use(middleware.CleanPath)
	s.Router.Use(middleware.RealIP)
	s.Router.Use(middleware.Recoverer)
	s.Router.Use(middleware.Logger)
	s.Router.Use(middleware.Timeout(60 * time.Minute))
	// Routes
	s.Router.Handle("/", playground.Handler("GraphQL playground", "/api/graphql"))
	s.Router.Route("/api", func(r chi.Router) {
		r.With(giggyMiddleware.Auth).Handle("/graphql", graphqlHandler)
		r.Group(func(r chi.Router) {
			r.Use(middleware.AllowContentType("application/json"))

			r.Handle("/ip", handlers.Ip())
			r.Handle("/mobile/signin", handlers.MobileSignin(signinController))
			r.Handle("/refresh/token", handlers.RefreshToken(userController))
			r.With(giggyMiddleware.Paystack).Handle("/webhook/paystack", webhook.Paystack())
		})
		r.Handle("/img/upload", handlers.ImageUploader())
	})
}
