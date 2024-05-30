package config

import (
	"os"
	"strings"

	"github.com/joho/godotenv"
)

var Configuration *configs

type configs struct {
	Server Server
	Ipinfo Ipinfo
}

func env() { godotenv.Load() }

func New() {
	env()

	c := configs{}
	c.Server = serverConfig()
	c.Ipinfo = ipinfoConfig()

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
