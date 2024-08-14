package aws

import (
	"context"
	"encoding/json"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/secretsmanager"
	vunoCfg "github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/logger"
	"github.com/sirupsen/logrus"
)

var (
	aws Aws
)

type PostgresSecret struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type Aws interface {
	SecretValueFromSecretManager(context.Context, *secretsmanager.GetSecretValueInput) (*PostgresSecret, error)
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
		log.WithError(err).Fatalln("aws: setup New instance")
	}

	svc := secretsmanager.NewFromConfig(cfg)

	aws = awsClient{log, svc}
}

func (aws awsClient) SecretValueFromSecretManager(ctx context.Context, input *secretsmanager.GetSecretValueInput) (*PostgresSecret, error) {
	var res *PostgresSecret
	result, err := aws.client.GetSecretValue(context.TODO(), input)
	if err != nil {
		aws.log.WithError(err).Error("aws: SecretValueFromSecretManager")
		return nil, err
	}

	var secretString string = *result.SecretString
	if err := json.Unmarshal([]byte(secretString), &res); err != nil {
		aws.log.WithError(err).Errorf("aws: json.Unmarshal secret res")
		return nil, err
	}

	return res, nil
}

func GetAwsService() Aws {
	return aws
}
