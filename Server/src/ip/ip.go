package ip

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net"
	"net/http"
	"time"

	copodCache "github.com/elc49/copod/Server/src/cache"
	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/ipinfo/go/v2/ipinfo"
	"github.com/ipinfo/go/v2/ipinfo/cache"
	"github.com/sirupsen/logrus"
)

type Ipinfo interface {
	GetIpinfo(ip string) (*model.Ipinfo, error)
}

var (
	ipClient Ipinfo
)

type ipC struct {
	client     *ipinfo.Client
	log        *logrus.Logger
	copodCache copodCache.Cache
	httpClient http.Client
}

func NewIpinfoClient() {
	client := ipinfo.NewClient(
		nil,
		ipinfo.NewCache(
			cache.NewInMemory().WithExpiration(time.Hour*24),
		),
		config.Configuration.Ipinfo.ApiKey,
	)

	ipClient = &ipC{client, logger.GetLogger(), copodCache.GetCache(), http.Client{}}
}

func (ipc ipC) GetIpinfo(ip string) (*model.Ipinfo, error) {
	ipinfo := &model.Ipinfo{}
	cacheValue, err := ipc.copodCache.Get(context.Background(), copodCache.IpCacheKey(ip), ipinfo)
	if err != nil {
		return nil, err
	} else if cacheValue != nil {
		return (cacheValue).(*model.Ipinfo), nil
	}

	req, err := http.NewRequest("GET", fmt.Sprintf("https://ipapi.co/%s/json/", ip), nil)
	req.Header.Set("User-Agent", "ipapi.co/#go-v1.5")
	if err != nil {
		ipc.log.WithError(err).Error("ip: htttp.NewRequest")
		return nil, err
	}

	res, err := ipc.httpClient.Do(req)
	if err != nil {
		ipc.log.WithError(err).Error("ip: ipApiClient.Do")
		return nil, err
	}

	defer res.Body.Close()
	body, err := io.ReadAll(res.Body)
	if err != nil {
		ipc.log.WithError(err).Error("ip: res.Body")
		return nil, err
	}

	err = json.Unmarshal(body, &ipinfo)
	if err != nil {
		ipc.log.WithError(err).Error("ip: json.Unmarshal")
		return nil, err
	}
	ipinfo.FarmingRightsFee = model.ServiceFeesByCountry("farming_rights", ipinfo.CountryCode)
	ipinfo.PosterRightsFee = model.ServiceFeesByCountry("poster_rights", ipinfo.CountryCode)

	secondaryIpinfo, err := ipc.client.GetIPInfo(net.ParseIP(ip))
	if err != nil {
		ipc.log.WithError(err).Error("ip: ipc.GetIPInfo")
		return nil, err
	}
	ipinfo.CountryFlagURL = secondaryIpinfo.CountryFlagURL
	ipinfo.Gps = secondaryIpinfo.Location

	go func() {
		if err := ipc.copodCache.Set(context.Background(), copodCache.IpCacheKey(ip), ipinfo, time.Hour*24); err != nil {
			ipc.log.WithFields(logrus.Fields{"value": ipinfo, "err": err}).Errorf("ip: cache ipinfo")
			return
		}
	}()

	return ipinfo, nil
}

func GetIpinfoClient() Ipinfo {
	return ipClient
}
