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

	Configuration = &c
	logrus.Infoln("Configurations...OK")
}

func serverConfig() Server {
	var config Server
	projectId := strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID"))
	serverEnv := strings.TrimSpace(os.Getenv("ENV"))

	port, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PORT",
		ProjectID:   projectId,
		Environment: serverEnv,
	})
	if err != nil {
		panic(err)
	}

	config.Port = port.SecretValue
	config.Env = serverEnv

	return config
}

func ipinfoConfig() Ipinfo {
	var config Ipinfo

	config.ApiKey = strings.TrimSpace(os.Getenv("IPINFO_SERVICE_API_KEY"))
	apiKey, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "IPINFO_SERVICE_API_KEY",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	config.ApiKey = apiKey.SecretValue

	return config
}

func rdbmsConfig() Rdbms {
	var config Rdbms

	driver, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_DATABASE_DRIVER",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	uri, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_DATABASE_URI",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	migrationFile, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTGRES_MIGRATION_FILE",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	migrateDb, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "MIGRATE_POSTGRES_DATABASE",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	forceMigrate, err := strconv.ParseBool(migrateDb.SecretValue)
	if err != nil {
		panic(err)
	}

	config.Driver = driver.SecretValue
	config.Uri = uri.SecretValue
	config.MigrationFile = migrationFile.SecretValue
	config.Migrate = forceMigrate

	return config
}

func jwtConfig() Jwt {
	var config Jwt

	expires, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "JWT_EXPIRES",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
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
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
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
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	baseUri, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "GOOGLE_CLOUD_BASE_OBJECT_URI",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	bucketName, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "GOOGLE_CLOUD_STORAGE_BUCKET",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
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
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
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
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	secretKey, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PAYSTACK_SECRET_KEY",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	paymentProvider, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PAYSTACK_PAYMENT_PROVIDER",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	testAccount, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "PAYSTACK_MOBILE_TEST_ACCOUNT",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
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

	posterFeesValue, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "POSTER_RIGHTS_FEE",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	posterFees, posterErr := strconv.Atoi(posterFeesValue.SecretValue)
	if posterErr != nil {
		panic(posterErr)
	}

	farmingFeesValue, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "FARMING_RIGHTS_FEE",
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
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
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
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
		Environment: strings.TrimSpace(os.Getenv("ENV")),
		ProjectID:   strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID")),
	})
	if err != nil {
		panic(err)
	}

	config.Dsn = dsn.SecretValue

	return config
}

func infisicalConfig() Infisical {
	var config Infisical
	infisicalProjectId := strings.TrimSpace(os.Getenv("INFISICAL_PROJECT_ID"))
	env := strings.TrimSpace(os.Getenv("ENV"))

	clientId, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "INFISICAL_CLIENT_ID",
		Environment: env,
		ProjectID:   infisicalProjectId,
	})
	if err != nil {
		panic(err)
	}

	clientSecret, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "INFISICAL_CLIENT_SECRET",
		Environment: env,
		ProjectID:   infisicalProjectId,
	})
	if err != nil {
		panic(err)
	}

	projectId, err := infisicalClient.Secrets().Retrieve(infisical.RetrieveSecretOptions{
		SecretKey:   "INFISICAL_PROJECT_ID",
		Environment: env,
		ProjectID:   infisicalProjectId,
	})
	if err != nil {
		panic(err)
	}

	config.ClientID = clientId.SecretValue
	config.ClientSecret = clientSecret.SecretValue
	config.ProjectID = projectId.SecretValue

	return config
}
