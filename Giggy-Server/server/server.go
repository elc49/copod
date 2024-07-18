package server

import (
	"net/http"
	"time"

	"github.com/99designs/gqlgen/graphql/handler"
	"github.com/99designs/gqlgen/graphql/handler/extension"
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
	"github.com/getsentry/sentry-go"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/gorilla/websocket"
	"github.com/sirupsen/logrus"
)

type Server struct {
	Router *chi.Mux
	Db     *db.Queries
}

func New() *Server {
	s := &Server{}
	s.Router = chi.NewRouter()
	s.config()
	s.Db = s.database(config.Configuration.Rdbms)
	s.services()
	s.sentrySetup()
	return s
}

func (s *Server) database(dbConfig config.Rdbms) *db.Queries {
	queries := postgres.Init(dbConfig)
	return queries
}

func (s *Server) config() { config.New() }

func (s *Server) services() {
	logger.New()
	cache.New()
	jwt.New(config.Jwt{
		Secret:  config.Configuration.Jwt.Secret,
		Expires: config.Configuration.Jwt.Expires,
	})
	ip.NewIpinfoClient()
	gcloud.New()
	paystack.New(s.Db)
}

func (s *Server) MountHandlers() {
	// Data controllers
	signinController := controllers.SigninController{}
	signinController.Init(s.Db)

	// GraphQL handler
	graphqlHandler := handler.New(graph.NewExecutableSchema(graph.New(s.Db, signinController)))
	graphqlHandler.AddTransport(&transport.POST{})
	graphqlHandler.AddTransport(&transport.Websocket{
		KeepAlivePingInterval: 10 * time.Second,
		Upgrader: websocket.Upgrader{
			CheckOrigin: func(r *http.Request) bool {
				return true
			},
		},
	})
	graphqlHandler.Use(extension.Introspection{})

	// Middlewares
	s.Router.Use(giggyMiddleware.Sentry)
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
		r.Handle("/subscription", graphqlHandler)
		r.Group(func(r chi.Router) {
			r.Use(middleware.AllowContentType("application/json"))

			r.Handle("/ip", handlers.Ip())
			r.Handle("/mobile/signin", handlers.MobileSignin(signinController))
			r.Handle("/refresh/token", handlers.RefreshToken(signinController))
			r.With(giggyMiddleware.Paystack).Handle("/webhook/paystack", webhook.Paystack())
		})
		r.Handle("/img/upload", handlers.ImageUploader())
	})
}

func (s *Server) isProd() bool {
	return config.Configuration.Server.Env == "production" ||
		config.Configuration.Server.Env == "staging"
}

func (s *Server) sentrySetup() {
	if s.isProd() {
		err := sentry.Init(sentry.ClientOptions{
			Dsn:              config.Configuration.Sentry.Dsn,
			EnableTracing:    true,
			TracesSampleRate: 1.0,
		})
		if err != nil {
			logrus.WithError(err).Errorf("server: sentry logging setup")
		}
	}
}
