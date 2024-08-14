package config

import (
	"os"
	"strconv"
	"strings"
	"time"

	infisical "github.com/infisical/go-sdk"
	"github.com/joho/godotenv"
	"github.com/sirupsen/logrus"
)

var (
	Configuration   *configs
	infisicalClient infisical.InfisicalClientInterface
)

type configs struct {
	Environment  string
	Server       Server
	Ipinfo       Ipinfo
	Rdbms        Rdbms
	Jwt          Jwt
	Gcloud       Gcloud
	RemoteAvatar string
	Paystack     Paystack
	Fees         Fees
	Redis        Redis
	Sentry       Sentry
	Infisical    Infisical
	Aws          Aws
}

func env() { godotenv.Load() }

func New() {
	logrus.Infoln("Collecting configurations...")
	env()
	clientId := strings.TrimSpace(os.Getenv("INFISICAL_CLIENT_ID"))
	clientSecret := strings.TrimSpace(os.Getenv("INFISICAL_CLIENT_SECRET"))

	c := configs{}
	c.Environment = strings.TrimSpace(os.Getenv("ENV"))

	infisicalClient = infisical.NewInfisicalClient(infisical.Config{})
	_, err := infisicalClient.Auth().UniversalAuthLogin(clientId, clientSecret)
	if err != nil {
		logrus.Errorf("Infisical auth failed: %v", err)
		panic(err)
	}

	c.Infisical = infisicalConfig()
	c.Server = serverConfig()
	c.Ipinfo = ipinfoConfig()
	c.Rdbms = rdbmsConfig()
	c.Jwt = jwtConfig()
	c.Gcloud = gcloudConfig()
	c.RemoteAvatar = remoteAvatarConfig()
	c.Paystack = paystackConfig()
	c.Fees = feesConfig()
	c.Redis = redisConfig()
	c.Sentry = sentryConfig()
	c.Aws = awsConfig()

	Configuration = &c
	logrus.Infoln("Configurations...OK")
}

func getEnv() string {
	env := strings.TrimSpace(os.Getenv("ENV"))
	if env == "" {
		return "dev"
	}

	return env
}

func getInfisicalProjectId() string {
	return strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID"))
}

func serverConfig() Server {
	var config Server

	port, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PORT",
		ProjectID:   getInfisicalProjectId(),
		Environment: getEnv(),
	})
	if err != nil {
		panic(err)
	}

	config.Port = port.SecretValue
	config.Env = getEnv()

	return config
}

func ipinfoConfig() Ipinfo {
	var config Ipinfo

	config.ApiKey = strings.TrimSpace(os.Getenv("IPINFO_SERVICE_API_KEY"))
	apiKey, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "IPINFO_SERVICE_API_KEY",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	config.ApiKey = apiKey.SecretValue

	return config
}

func postgresConfig() Postgres {
	var config Postgres

	dbName, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_NAME",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	pass := infisical.Secret{}
	if getEnv() != "prod" {
		pass, err = infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
			SecretKey:   "POSTGRES_PASSWORD",
			Environment: getEnv(),
			ProjectID:   getInfisicalProjectId(),
		})
		if err != nil {
			panic(err)
		}
	}

	user, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_USER",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	postgresWriter, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_WRITER",
		ProjectID:   getInfisicalProjectId(),
		Environment: getEnv(),
	})
	if err != nil {
		panic(err)
	}

	postgresReader, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_READER",
		ProjectID:   getInfisicalProjectId(),
		Environment: getEnv(),
	})
	if err != nil {
		panic(err)
	}

	migrateDb, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_MIGRATE",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	forceMigrate, err := strconv.ParseBool(migrateDb.SecretValue)
	if err != nil {
		panic(err)
	}

	migration, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_MIGRATION",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	driver, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_DRIVER",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	port, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_PORT",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	config.DbName = dbName.SecretValue
	config.DbUser = user.SecretValue
	config.WriterHost = postgresWriter.SecretValue
	config.ReaderHost = postgresReader.SecretValue
	config.DbPass = pass.SecretValue
	config.Migrate = forceMigrate
	config.Migration = migration.SecretValue
	config.Driver = driver.SecretValue
	config.Port = port.SecretValue

	return config
}

func rdbmsConfig() Rdbms {
	var config Rdbms

	config.Postgres = postgresConfig()

	return config
}

func jwtConfig() Jwt {
	var config Jwt

	expires, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "JWT_EXPIRES",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	expire, err := time.ParseDuration(expires.SecretValue)
	if err != nil {
		panic(err)
	}

	secret, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "JWT_SECRET",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	config.Expires = expire
	config.Secret = secret.SecretValue

	return config
}

func gcloudConfig() Gcloud {
	var config Gcloud

	googleAdc, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "GOOGLE_ADC",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	baseUri, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "GOOGLE_CLOUD_BASE_OBJECT_URI",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	bucketName, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "GOOGLE_CLOUD_STORAGE_BUCKET",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	config.BucketObjectBaseUri = baseUri.SecretValue
	config.StorageBucketName = bucketName.SecretValue
	config.Adc = googleAdc.SecretValue

	return config
}

func remoteAvatarConfig() string {
	url, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "BASE_REMOTE_AVATAR_URL",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	return url.SecretValue
}

func paystackConfig() Paystack {
	var config Paystack

	baseApi, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PAYSTACK_BASE_API",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	secretKey, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PAYSTACK_SECRET_KEY",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	paymentProvider, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PAYSTACK_PAYMENT_PROVIDER",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	testAccount, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PAYSTACK_MOBILE_TEST_ACCOUNT",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	config.BaseApi = baseApi.SecretValue
	config.SecretKey = secretKey.SecretValue
	config.Provider = paymentProvider.SecretValue
	config.MobileTestAccount = testAccount.SecretValue
	if strings.Contains(config.SecretKey, "sk_test_") {
		config.Env = "test"
	} else if strings.Contains(config.SecretKey, "sk_live_") {
		config.Env = "live"
	}

	return config
}

func feesConfig() Fees {
	var config Fees
	opts := infisical.RetrieveSecretOptions{
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	}

	opts.SecretKey = "POSTER_RIGHTS_FEE"
	posterFeesValue, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	posterFees, posterErr := strconv.Atoi(posterFeesValue.SecretValue)
	if posterErr != nil {
		panic(posterErr)
	}

	opts.SecretKey = "FARMING_RIGHTS_FEE"
	farmingFeesValue, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	farmingFees, farmingErr := strconv.Atoi(farmingFeesValue.SecretValue)
	if farmingErr != nil {
		panic(farmingErr)
	}

	config.PosterRights = posterFees
	config.FarmingRights = farmingFees

	return config
}

func redisConfig() Redis {
	var config Redis

	url, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "REDIS_ENDPOINT",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	config.Url = url.SecretValue

	return config
}

func sentryConfig() Sentry {
	var config Sentry

	dsn, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "SENTRY_DSN",
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	})
	if err != nil {
		panic(err)
	}

	config.Dsn = dsn.SecretValue

	return config
}

func infisicalConfig() Infisical {
	var config Infisical
	opts := infisical.RetrieveSecretOptions{
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	}

	opts.SecretKey = "INFISICAL_CLIENT_ID"
	clientId, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	opts.SecretKey = "INFISICAL_CLIENT_SECRET"
	clientSecret, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	opts.SecretKey = "INFISICAL_PROJECT_ID"
	projectId, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	config.ClientID = clientId.SecretValue
	config.ClientSecret = clientSecret.SecretValue
	config.ProjectID = projectId.SecretValue

	return config
}

func awsConfig() Aws {
	var config Aws
	opts := infisical.RetrieveSecretOptions{
		Environment: getEnv(),
		ProjectID:   getInfisicalProjectId(),
	}

	opts.SecretKey = "AWS_ACCESS_KEY"
	accessKey, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	opts.SecretKey = "AWS_SECRET_ACCESS_KEY"
	secretAccessKey, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	opts.SecretKey = "AWS_REGION"
	awsRegion, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	opts.SecretKey = "POSTGRES_SECRET_NAME"
	secretName, err := infisicalClient.Secrets().Retrieve(opts)
	if err != nil {
		panic(err)
	}

	config.AccessKey = accessKey.SecretValue
	config.SecretAccessKey = secretAccessKey.SecretValue
	config.Region = awsRegion.SecretValue
	config.PostgresSecretName = secretName.SecretValue

	return config
}
