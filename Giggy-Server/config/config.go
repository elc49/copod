package config

import (
	"os"
	"strconv"
	"strings"
	"time"

	"github.com/joho/godotenv"
)

var Configuration *configs

type configs struct {
	Server       Server
	Ipinfo       Ipinfo
	Rdbms        Rdbms
	Jwt          Jwt
	Gcloud       Gcloud
	RemoteAvatar string
	Paystack     Paystack
	Fees         Fees
}

func env() { godotenv.Load() }

func New() {
	env()

	c := configs{}
	c.Server = serverConfig()
	c.Ipinfo = ipinfoConfig()
	c.Rdbms = rdbmsConfig()
	c.Jwt = jwtConfig()
	c.Gcloud = gcloudConfig()
	c.RemoteAvatar = remoteAvatarConfig()
	c.Paystack = paystackConfig()
	c.Fees = feesConfig()

	Configuration = &c
}

func serverConfig() Server {
	var config Server

	config.Port = strings.TrimSpace(os.Getenv("PORT"))
	config.Env = strings.TrimSpace(os.Getenv("ENV"))

	return config
}

func ipinfoConfig() Ipinfo {
	var config Ipinfo

	config.ApiKey = strings.TrimSpace(os.Getenv("IPINFO_SERVICE_API_KEY"))

	return config
}

func rdbmsConfig() Rdbms {
	var config Rdbms

	config.Driver = strings.TrimSpace(os.Getenv("POSTGRES_DATABASE_DRIVER"))
	config.Uri = strings.TrimSpace(os.Getenv("POSTGRES_DATABASE_URI"))
	config.MigrationFile = strings.TrimSpace(os.Getenv("POSTGRES_MIGRATION_FILE"))

	forceMigrate, err := strconv.ParseBool(strings.TrimSpace(os.Getenv("MIGRATE_POSTGRES_DATABASE")))
	if err != nil {
		panic(err)
	}
	config.Migrate = forceMigrate

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

func remoteAvatarConfig() string {
	var url string

	url = strings.TrimSpace(os.Getenv("BASE_REMOTE_AVATAR_URL"))

	return url
}

func paystackConfig() Paystack {
	var config Paystack

	config.BaseApi = strings.TrimSpace(os.Getenv("PAYSTACK_BASE_API"))
	config.SecretKey = strings.TrimSpace(os.Getenv("PAYSTACK_SECRET_KEY"))
	config.Provider = strings.TrimSpace(os.Getenv("PAYSTACK_PAYMENT_PROVIDER"))
	config.TestAccount = strings.TrimSpace(os.Getenv("PAYSTACK_TEST_ACCOUNT"))

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
