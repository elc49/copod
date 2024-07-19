package cache

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
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
	ctx := context.Background()
	opt, err := redis.ParseURL(config.Configuration.Redis.Url)
	if err != nil {
		log.WithError(err).Fatalln("new cache client")
	}

	rdb := redis.NewClient(opt)
	if err := rdb.Ping(ctx).Err(); err != nil {
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
