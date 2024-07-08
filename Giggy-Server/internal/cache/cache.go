package cache

import (
	"context"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
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
}

func New() {
	ctx := context.Background()
	opt, err := redis.ParseURL(config.Configuration.Redis.Url)
	if err != nil {
		logrus.WithError(err).Fatalln("new cache client")
	}

	rdb := redis.NewClient(opt)
	if err := rdb.Ping(ctx).Err(); err != nil {
		logrus.WithError(err).Fatalln("ping: redis conn")
	}

	c = &cacheClient{rdb}
}

func GetCache() Cache {
	return c
}

func (c *cacheClient) GetRedis() *redis.Client {
	return c.client
}
