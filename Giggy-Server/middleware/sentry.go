package middleware

import (
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	sentryHttp "github.com/getsentry/sentry-go/http"
)

func Sentry(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if isProd() {
			sentryHttp.New(sentryHttp.Options{}).Handle(next).ServeHTTP(w, r)
		} else {
			next.ServeHTTP(w, r)
		}
	})
}

func isProd() bool {
	return config.Configuration.Server.Env == "staging" ||
		config.Configuration.Server.Env == "production"
}
