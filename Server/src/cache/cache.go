package cache

import (
	"context"
	"encoding/json"
	"time"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/logger"
	"github.com/redis/go-redis/v9"
	"github.com/sirupsen/logrus"
)

var (
	c Cache
)

type Cache interface {
	Get(ctx context.Context, key string, returnValue interface{}) (interface{}, error)
	Set(ctx context.Context, key string, value interface{}, expires time.Duration) error
	GetRedis() *redis.Client
}

type cacheClient struct {
	redisClient *redis.Client
	log         *logrus.Logger
}

func New() {
	log := logger.GetLogger()
	opt, err := redis.ParseURL(config.Configuration.Redis.Url)
	if err != nil {
		log.WithError(err).Fatalln("cache: new cache client")
	}

	rdb := redis.NewClient(opt)
	if err := rdb.Ping(context.Background()).Err(); err != nil {
		log.WithError(err).Fatalln("ping: redis conn")
	}

	c = &cacheClient{rdb, log}
}

func (c *cacheClient) Get(ctx context.Context, key string, returnValue interface{}) (interface{}, error) {
	result, err := c.redisClient.Get(ctx, key).Result()
	if err == redis.Nil {
		return nil, nil
	} else if err != nil {
		c.log.WithFields(logrus.Fields{"err": err, "key": key}).Errorf("cache: get value")
		return nil, err
	}

	if err := json.Unmarshal([]byte(result), returnValue); err != nil {
		c.log.WithFields(logrus.Fields{"err": err, "value": returnValue}).Errorf("cache: unmarshal cache return value")
		return nil, err
	}

	return returnValue, nil
}

func (c *cacheClient) Set(ctx context.Context, key string, value interface{}, expires time.Duration) error {
	m, err := json.Marshal(value)
	if err != nil {
		c.log.WithFields(logrus.Fields{"key": key, "value": value}).Errorf("cache: marshal cache value")
		return err
	}

	return c.redisClient.Set(ctx, key, m, expires).Err()
}

func GetCache() Cache {
	return c
}

func (c *cacheClient) GetRedis() *redis.Client {
	return c.redisClient
}
