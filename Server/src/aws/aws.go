package aws

import (
	"context"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
	vunoCfg "github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/logger"
	"github.com/sirupsen/logrus"
)

var aws Aws

type Aws interface {
	SecretValueFromSecretManager(context.Context, *secretsmanager.GetSecretValueInput) (string, error)
}

type awsClient struct {
	log    *logrus.Logger
	client *secretsmanager.Client
}

func New() {
	log := logger.GetLogger()
	vunoCfg := vunoCfg.Configuration
	cfg, err := config.LoadDefaultConfig(
		context.TODO(),
		config.WithRegion(vunoCfg.Aws.Region),
		config.WithCredentialsProvider(
			credentials.NewStaticCredentialsProvider(vunoCfg.Aws.AccessKey, vunoCfg.Aws.SecretAccessKey, ""),
		),
	)
	if err != nil {
		log.WithError(err).Error("aws: setup New instance")
	}

	svc := secretsmanager.NewFromConfig(cfg)

	aws = &awsClient{log, svc}
}

func (a *awsClient) SecretValueFromSecretManager(ctx context.Context, input *secretsmanager.GetSecretValueInput) (string, error) {
	result, err := a.client.GetSecretValue(context.TODO(), input)
	if err != nil {
		a.log.WithError(err).Error("aws: SecretValueFromSecretManager")
		return "", err
	}

	var secretString string = *result.SecretString
	return secretString, nil
}

func GetAwsService() Aws {
	return aws
}
