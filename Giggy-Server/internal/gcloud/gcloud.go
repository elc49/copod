package gcloud

import (
	"context"
	"encoding/base64"
	"fmt"
	"io"
	"mime/multipart"

	"cloud.google.com/go/storage"
	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	log "github.com/sirupsen/logrus"
	"google.golang.org/api/option"
)

var (
	gc Gcloud
)

type Gcloud interface {
	UploadPostImage(ctx context.Context, file multipart.File, fileHeader *multipart.FileHeader) (string, error)
}

type gcloudClient struct {
	storage             *storage.Client
	storageBucket       string
	baseBucketObjectUri string
}

func New() {
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
	}
}

func GetGcloudService() Gcloud {
	return gc
}

func (gC gcloudClient) UploadPostImage(ctx context.Context, file multipart.File, fileHeader *multipart.FileHeader) (string, error) {
	storageWriter := gC.storage.Bucket(gC.storageBucket).Object(fileHeader.Filename).NewWriter(ctx)

	if _, err := io.Copy(storageWriter, file); err != nil {
		return "", err
	}

	if err := storageWriter.Close(); err != nil {
		return "", err
	}

	return fmt.Sprintf("%s/%s/%s", gC.baseBucketObjectUri, gC.storageBucket, fileHeader.Filename), nil
}
