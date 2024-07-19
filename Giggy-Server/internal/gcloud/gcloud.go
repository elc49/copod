package gcloud

import (
	"context"
	"encoding/base64"
	"fmt"
	"io"
	"mime/multipart"

	"cloud.google.com/go/storage"
	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/util"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/sirupsen/logrus"
	"google.golang.org/api/option"
)

var (
	gc Gcloud
)

type Gcloud interface {
	UploadPostImage(ctx context.Context, file multipart.File, fileHeader *multipart.FileHeader) (string, error)
	ReadFromRemote(ctx context.Context, body io.Reader) (string, error)
}

type gcloudClient struct {
	storage             *storage.Client
	storageBucket       string
	baseBucketObjectUri string
	log                 *logrus.Logger
}

func New() {
	log := logger.GetLogger()
	credentials, err := base64.StdEncoding.DecodeString(config.Configuration.Gcloud.Adc)
	if err != nil {
		log.WithError(err).Fatalln("gcloud: New()")
	}

	storage, err := storage.NewClient(context.Background(), option.WithCredentialsJSON(credentials))
	if err != nil {
		log.WithError(err).Fatalln("gcloud: New()")
	}

	gc = gcloudClient{
		storage,
		config.Configuration.Gcloud.StorageBucketName,
		config.Configuration.Gcloud.BucketObjectBaseUri,
		log,
	}
}

func GetGcloudService() Gcloud {
	return gc
}

func (gC gcloudClient) UploadPostImage(ctx context.Context, file multipart.File, fileHeader *multipart.FileHeader) (string, error) {
	storageWriter := gC.storage.Bucket(gC.storageBucket).Object(fileHeader.Filename).NewWriter(ctx)

	if _, err := io.Copy(storageWriter, file); err != nil {
		gC.log.WithError(err).Errorf("gcloud: copy storageWriter")
		return "", err
	}

	if err := storageWriter.Close(); err != nil {
		gC.log.WithError(err).Errorf("gcloud: close storageWriter")
		return "", err
	}

	return fmt.Sprintf("%s/%s/%s", gC.baseBucketObjectUri, gC.storageBucket, fileHeader.Filename), nil
}

func (gC gcloudClient) ReadFromRemote(ctx context.Context, body io.Reader) (string, error) {
	fileName := fmt.Sprintf("avatar_%s.svg", util.RandomStringByLength(5))
	storageW := gC.storage.Bucket(gC.storageBucket).Object(fileName).NewWriter(ctx)

	if _, err := io.Copy(storageW, body); err != nil {
		gC.log.WithError(err).Errorf("gcloud: copy storageW")
		return "", err
	}

	if err := storageW.Close(); err != nil {
		gC.log.WithError(err).Errorf("gcloud: close storageWriter")
		return "", err
	}

	return fmt.Sprintf("%s/%s/%s", gC.baseBucketObjectUri, gC.storageBucket, fileName), nil
}
