package logger

import (
	"time"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/getsentry/sentry-go"
	logrusSentry "github.com/getsentry/sentry-go/logrus"
	"github.com/sirupsen/logrus"
)

var log = logrus.New()

func New() {
	isProd := func() bool {
		if config.Configuration == nil {
			return false
		} else if config.Configuration.Server.Env == "production" {
			return true
		} else {
			return false
		}
	}

	if isProd() {
		// Error levels to report
		levels := []logrus.Level{
			logrus.PanicLevel,
			logrus.ErrorLevel,
			logrus.FatalLevel,
		}
		hook, err := logrusSentry.New(levels, sentry.ClientOptions{
			Dsn:              config.Configuration.Sentry.Dsn,
			AttachStacktrace: true,
		})
		if err != nil {
			logrus.WithError(err).Fatalln("logger: add sentry hook")
		}

		log.AddHook(hook)

		defer hook.Flush(5 * time.Second)
		logrus.RegisterExitHandler(func() { hook.Flush(5 * time.Second) })
	}
}

func GetLogger() *logrus.Logger {
	return log
}
