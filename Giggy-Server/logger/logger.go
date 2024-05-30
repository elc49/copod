package logger

import "github.com/sirupsen/logrus"

var log *logrus.Logger

func New() {
	log = logrus.New()
}

func GetLogger() *logrus.Logger {
	return log
}
