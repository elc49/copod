package gcloud

import (
	"bytes"
	"context"
	"encoding/base64"
	"fmt"
	"image/png"
	"io"
	"mime/multipart"

	"cloud.google.com/go/storage"
	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/util"
	"github.com/sirupsen/logrus"
	"google.golang.org/api/option"
)

var (
	gc Gcloud
)

type Gcloud interface {
	UploadPostImage(context.Context, multipart.File, *multipart.FileHeader) (string, error)
	GenerateRandomAvatar(context.Context, string) (string, error)
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

func (gC gcloudClient) uploadToGCS(ctx context.Context, bucketName, objectName string, r *bytes.Buffer) error {
	w := gC.storage.Bucket(bucketName).Object(objectName).NewWriter(ctx)

	if _, err := io.Copy(w, r); err != nil {
		gC.log.WithError(err).Errorf("gcloud: uploadToGCS")
		return err
	}

	if err := w.Close(); err != nil {
		gC.log.WithError(err).Errorf("gcloud: close storage writer")
		return err
	}

	return nil
}

func (gC gcloudClient) GenerateRandomAvatar(ctx context.Context, randString string) (string, error) {
	img := util.RandomAvatar(200, 200)
	fileName := fmt.Sprintf("avatar_%s.png", randString)

	// Buffer to hold image data
	var buf bytes.Buffer
	if err := png.Encode(&buf, img); err != nil {
		gC.log.WithError(err).Errorf("gcloud: GenerateAvatar")
		return "", err
	}

	// Upload to google cloud storage
	if err := gC.uploadToGCS(ctx, gC.storageBucket, fileName, &buf); err != nil {
		return "", err
	}

	// Object url
	return fmt.Sprintf("%s/%s/%s", gC.baseBucketObjectUri, gC.storageBucket, fileName), nil
}
