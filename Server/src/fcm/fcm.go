package fcm

import (
	"context"
	"encoding/base64"
	"sync"

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
	mu     sync.Mutex
}

func NewFcm() {
	log := logger.GetLogger()
	credentials, err := base64.StdEncoding.DecodeString(config.Configuration.Gcloud.Adc)
	if err != nil {
		log.WithError(err).Fatalln("fcm: New()")
	}

	cfg := &firebase.Config{
		ProjectID: config.Configuration.Gcloud.FirebaseProjectID,
	}
	app, err := firebase.NewApp(context.Background(), cfg, option.WithCredentialsJSON(credentials))
	if err != nil {
		log.WithError(err).Fatalln("fcm: NewApp()")
	}

	c, err := app.Messaging(context.Background())
	if err != nil {
		log.WithError(err).Fatalln("fcm: Messaging()")
	}

	fcm = &fcmClient{c, log, sync.Mutex{}}
	log.Infoln("firebase cloud messaging client...OK")
}

func GetFCMService() FCMInterface {
	return fcm
}

func (fcm *fcmClient) Notify(ctx context.Context, msg *messaging.Message) (*string, error) {
	fcm.mu.Lock()
	defer fcm.mu.Unlock()

	response, err := fcm.client.Send(ctx, msg)
	if err != nil {
		fcm.logger.WithFields(logrus.Fields{"payload": msg}).WithError(err).Errorf("fcm: Notify()")
		return nil, err
	}
	fcm.logger.WithFields(logrus.Fields{"payload": msg}).Info("fcm: notify success")

	return &response, nil
}
