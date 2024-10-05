package server

import (
	"embed"
	"net/http"
	"time"

	"github.com/99designs/gqlgen/graphql/handler"
	"github.com/99designs/gqlgen/graphql/handler/extension"
	"github.com/99designs/gqlgen/graphql/handler/transport"
	"github.com/99designs/gqlgen/graphql/playground"
	"github.com/elc49/copod/Server/src/cache"
	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/fcm"
	"github.com/elc49/copod/Server/src/gcloud"
	"github.com/elc49/copod/Server/src/graph"
	"github.com/elc49/copod/Server/src/handlers"
	webhook "github.com/elc49/copod/Server/src/handlers/webhook"
	"github.com/elc49/copod/Server/src/ip"
	"github.com/elc49/copod/Server/src/jwt"
	"github.com/elc49/copod/Server/src/logger"
	giggyMiddleware "github.com/elc49/copod/Server/src/middleware"
	"github.com/elc49/copod/Server/src/paystack"
	"github.com/elc49/copod/Server/src/postgres"
	"github.com/getsentry/sentry-go"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/gorilla/websocket"
	"github.com/sirupsen/logrus"
)

type Server struct {
	Router *chi.Mux
	Store  postgres.Store
}

func New() *Server {
	s := &Server{}
	s.Router = chi.NewRouter()
	s.config()
	s.Store = s.database(config.Configuration.Rdbms)
	s.services()
	s.sentrySetup()
	return s
}

func (s *Server) database(option config.Rdbms) postgres.Store {
	queries := postgres.Store{
		StoreReader: postgres.InitReader(option),
		StoreWriter: postgres.InitWriter(option),
	}

	return queries
}

func (s *Server) config() {
	config.New()
	logger.New()
}

func (s *Server) services() {
	cache.New()
	jwt.New(config.Jwt{
		Secret:  config.Configuration.Jwt.Secret,
		Expires: config.Configuration.Jwt.Expires,
	})
	ip.NewIpinfoClient()
	gcloud.New()
	paystack.New(s.Store)

	// Enable firebase cloud messaging in prod/staging
	if config.IsProd() {
		fcm.NewFcm()
	}
}

func (s *Server) MountHandlers(static embed.FS) {
	// Data controllers
	signinController := controllers.SigninController{}
	signinController.Init(s.Store)

	// GraphQL handler
	graphqlHandler := handler.New(graph.NewExecutableSchema(graph.New(s.Store, signinController)))
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
			r.Handle("/refresh/notification_id", handlers.RecycleUserFCMToken())
		})
		r.Handle("/img/upload", handlers.ImageUploader())
	})
	s.Router.Handle("/privacy", handlers.Privacy())
	s.Router.Handle("/favicon.ico", handlers.Favicon())
	s.Router.Handle("/static/*", http.FileServer(http.FS(static)))
}

func (s *Server) sentrySetup() {
	if config.IsProd() {
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
