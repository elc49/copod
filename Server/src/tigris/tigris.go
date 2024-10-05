package tigris

import (
	"bytes"
	"context"
	"mime/multipart"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/feature/s3/manager"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	copodConfig "github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/sirupsen/logrus"
)

var svc Tigris

type Tigris interface {
	UploadImage(context.Context, multipart.File, *multipart.FileHeader) (*string, error)
}

type tigrisClient struct {
	log      *logrus.Logger
	s3Client *manager.Uploader
}

func GetTrigrisService() Tigris {
	return svc
}

func NewClient() {
	log := logger.GetLogger()
	sdkConfig, err := config.LoadDefaultConfig(context.TODO(), config.WithCredentialsProvider(
		credentials.NewStaticCredentialsProvider(
			copodConfig.Configuration.Tigris.AccessKeyId,
			copodConfig.Configuration.Tigris.SecretAccessKey,
			"",
		)))
	if err != nil {
		log.WithError(err).Fatalln("tigris: LoadDefaultConfig")
	}

	sC := s3.NewFromConfig(sdkConfig, func(o *s3.Options) {
		o.Region = copodConfig.Configuration.Tigris.Region
		o.BaseEndpoint = aws.String(copodConfig.Configuration.Tigris.EndpointUrl)
	})

	svc = &tigrisClient{log, manager.NewUploader(sC)}
}

func (tc *tigrisClient) UploadImage(ctx context.Context, file multipart.File, fileHeader *multipart.FileHeader) (*string, error) {
	fileBuffer := make([]byte, fileHeader.Size)
	file.Read(fileBuffer)
	params := &s3.PutObjectInput{
		Bucket: aws.String(copodConfig.Configuration.Tigris.BucketName),
		Key:    aws.String(fileHeader.Filename),
		Body:   bytes.NewReader(fileBuffer),
	}

	res, err := tc.s3Client.Upload(ctx, params)
	if err != nil {
		tc.log.WithFields(logrus.Fields{"fileSize": fileHeader.Size}).WithError(err).Errorf("tigris: s3 PutObject")
		return nil, err
	}

	return &res.Location, nil
}
