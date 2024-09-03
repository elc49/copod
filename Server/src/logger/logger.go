package logger

import (
	"time"

	"github.com/elc49/copod/Server/src/config"
	"github.com/getsentry/sentry-go"
	logrusSentry "github.com/getsentry/sentry-go/logrus"
	"github.com/sirupsen/logrus"
)

var log = logrus.New()

func New() {
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

func isProd() bool {
	if config.Configuration == nil {
		return false
	}

	return config.Configuration.Server.Env == "prod" ||
		config.Configuration.Server.Env == "staging"
}

func GetLogger() *logrus.Logger {
	return log
}
