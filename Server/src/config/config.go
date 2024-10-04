package config

import (
	"os"
	"strconv"
	"strings"
	"time"

	"github.com/joho/godotenv"
	"github.com/sirupsen/logrus"
)

var (
	Configuration *configs
)

type configs struct {
	Environment string
	Server      Server
	Ipinfo      Ipinfo
	Rdbms       Rdbms
	Jwt         Jwt
	Gcloud      Gcloud
	Paystack    Paystack
	Fees        Fees
	Redis       Redis
	Sentry      Sentry
}

func env() { godotenv.Load() }

func New() {
	logrus.Infoln("Collecting configurations...")
	env()
	c := configs{}
	c.Environment = getEnv()

	c.Server = serverConfig()
	c.Ipinfo = ipinfoConfig()
	c.Rdbms = rdbmsConfig()
	c.Jwt = jwtConfig()
	c.Gcloud = gcloudConfig()
	c.Paystack = paystackConfig()
	c.Fees = feesConfig()
	c.Redis = redisConfig()
	c.Sentry = sentryConfig()

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

func serverConfig() Server {
	var config Server

	config.Env = strings.TrimSpace(os.Getenv("ENV"))
	config.Port = strings.TrimSpace(os.Getenv("PORT"))

	return config
}

func ipinfoConfig() Ipinfo {
	var config Ipinfo

	config.ApiKey = strings.TrimSpace(os.Getenv("IPINFO_SERVICE_API_KEY"))

	return config
}

func postgresConfig() Postgres {
	var config Postgres

	config.DbName = strings.TrimSpace(os.Getenv("POSTGRES_NAME"))
	config.DbUser = strings.TrimSpace(os.Getenv("POSTGRES_USER"))
	config.Driver = strings.TrimSpace(os.Getenv("POSTGRES_DRIVER"))
	config.WriterHost = strings.TrimSpace(os.Getenv("POSTGRES_WRITER"))
	config.ReaderHost = strings.TrimSpace(os.Getenv("POSTGRES_READER"))
	migrate := strings.TrimSpace(os.Getenv("POSTGRES_MIGRATE"))
	forceMigrate, err := strconv.ParseBool(migrate)
	if err != nil {
		panic(err)
	}
	config.Migrate = forceMigrate
	config.Migration = strings.TrimSpace(os.Getenv("POSTGRES_MIGRATION"))
	config.DbPass = strings.TrimSpace(os.Getenv("POSTGRES_PASS"))

	return config
}

func rdbmsConfig() Rdbms {
	var config Rdbms

	config.Postgres = postgresConfig()

	return config
}

func jwtConfig() Jwt {
	var config Jwt

	expire, err := time.ParseDuration(strings.TrimSpace(os.Getenv("JWT_EXPIRES")))
	if err != nil {
		panic(err)
	}
	config.Expires = expire
	config.Secret = strings.TrimSpace(os.Getenv("JWT_SECRET"))

	return config
}

func gcloudConfig() Gcloud {
	var config Gcloud

	config.Adc = strings.TrimSpace(os.Getenv("GOOGLE_ADC"))
	config.BucketObjectBaseUri = strings.TrimSpace(os.Getenv("GOOGLE_CLOUD_BASE_OBJECT_URI"))
	config.StorageBucketName = strings.TrimSpace(os.Getenv("GOOGLE_CLOUD_STORAGE_BUCKET"))

	return config
}

func paystackConfig() Paystack {
	var config Paystack

	config.BaseApi = strings.TrimSpace(os.Getenv("PAYSTACK_BASE_API"))
	config.SecretKey = strings.TrimSpace(os.Getenv("PAYSTACK_SECRET_KEY"))
	config.Provider = strings.TrimSpace(os.Getenv("PAYSTACK_PAYMENT_PROVIDER"))
	config.MobileTestAccount = strings.TrimSpace(os.Getenv("PAYSTACK_MOBILE_TEST_ACCOUNT"))
	if strings.Contains(config.SecretKey, "sk_test_") {
		config.Env = "test"
	} else if strings.Contains(config.SecretKey, "sk_live_") {
		config.Env = "live"
	}

	return config
}

func feesConfig() Fees {
	var config Fees

	posterFees, posterErr := strconv.Atoi(strings.TrimSpace(os.Getenv("POSTER_RIGHTS_FEE")))
	if posterErr != nil {
		panic(posterErr)
	}
	farmingFees, farmingErr := strconv.Atoi(strings.TrimSpace(os.Getenv("FARMING_RIGHTS_FEE")))
	if farmingErr != nil {
		panic(farmingErr)
	}

	config.PosterRights = posterFees
	config.FarmingRights = farmingFees

	return config
}

func redisConfig() Redis {
	var config Redis

	config.Url = strings.TrimSpace(os.Getenv("REDIS_ENDPOINT"))

	return config
}

func sentryConfig() Sentry {
	var config Sentry

	config.Dsn = strings.TrimSpace(os.Getenv("SENTRY_DSN"))

	return config
}

// Runtime check to server env
func IsProd() bool {
	env := getEnv()

	// Haven't figured how to init in test environment
	if Configuration == nil {
		return false
	}

	return env == "prod" || env == "staging"
}
