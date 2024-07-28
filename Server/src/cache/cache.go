package cache

import (
	"context"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/logger"
	"github.com/redis/go-redis/v9"
	"github.com/sirupsen/logrus"
)

var (
	c Cache
)

type Cache interface {
	GetRedis() *redis.Client
}

type cacheClient struct {
	client *redis.Client
	log    *logrus.Logger
}

func New() {
	log := logger.GetLogger()
	opt, err := redis.ParseURL(config.Configuration.Redis.Url)
	if err != nil {
		log.WithError(err).Fatalln("new cache client")
	}

	rdb := redis.NewClient(opt)
	if err := rdb.Ping(context.Background()).Err(); err != nil {
		log.WithError(err).Fatalln("ping: redis conn")
	}

	c = &cacheClient{rdb, log}
}

func GetCache() Cache {
	return c
}

func (c *cacheClient) GetRedis() *redis.Client {
	return c.client
}
