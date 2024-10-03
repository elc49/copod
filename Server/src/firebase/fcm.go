package fcm

import (
	"context"
	"encoding/base64"

	firebase "firebase.google.com/go"
	"firebase.google.com/go/messaging"
	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/sirupsen/logrus"
	"google.golang.org/api/option"
)

var fcm FCMInterface

type FCMInterface interface {
	Notify(context.Context, *messaging.Message) (*string, error)
}

type fcmClient struct {
	client *messaging.Client
	logger *logrus.Logger
}

func NewFcm() {
	log := logger.GetLogger()
	credentials, err := base64.StdEncoding.DecodeString(config.Configuration.Gcloud.Adc)
	if err != nil {
		log.WithError(err).Fatalln("fcm: New()")
	}

	opt := []option.ClientOption{option.WithCredentialsJSON(credentials)}
	app, err := firebase.NewApp(context.Background(), nil, opt...)
	if err != nil {
		log.WithError(err).Fatalln("fcm: NewApp()")
	}

	c, err := app.Messaging(context.Background())
	if err != nil {
		log.WithError(err).Fatalln("fcm: Messaging()")
	}

	fcm = &fcmClient{c, log}
}

func (fcm *fcmClient) Notify(ctx context.Context, msg *messaging.Message) (*string, error) {
	response, err := fcm.client.Send(ctx, msg)
	if err != nil {
		fcm.logger.WithFields(logrus.Fields{"payload": msg}).WithError(err).Errorf("fcm: Notify()")
		return nil, err
	}
	fcm.logger.WithFields(logrus.Fields{"payload": msg}).Info("fcm: notify success")

	return &response, nil
}
