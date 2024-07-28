package ip

import (
	"encoding/json"
	"fmt"
	"io"
	"net"
	"net/http"
	"time"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/logger"
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
	client *ipinfo.Client
	log    *logrus.Logger
}

func NewIpinfoClient() {
	client := ipinfo.NewClient(
		nil,
		ipinfo.NewCache(
			cache.NewInMemory().WithExpiration(time.Hour*24),
		),
		config.Configuration.Ipinfo.ApiKey,
	)

	ipClient = &ipC{client, logger.GetLogger()}
}

func (ipc ipC) GetIpinfo(ip string) (*model.Ipinfo, error) {
	ipinfo := &model.Ipinfo{}
	ipinfo.FarmingRightsFee = config.Configuration.Fees.FarmingRights
	ipinfo.PosterRightsFee = config.Configuration.Fees.PosterRights

	ipApiClient := http.Client{}
	req, err := http.NewRequest("GET", fmt.Sprintf("https://ipapi.co/%s/json/", ip), nil)
	req.Header.Set("User-Agent", "ipapi.co/#go-v1.5")
	if err != nil {
		ipc.log.WithError(err).Error("ip: htttp.NewRequest")
		return nil, err
	}

	res, err := ipApiClient.Do(req)
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

	secondaryIpinfo, err := ipc.client.GetIPInfo(net.ParseIP(ip))
	if err != nil {
		ipc.log.WithError(err).Error("ip: ipc.GetIPInfo")
		return nil, err
	}
	ipinfo.CountryFlagURL = secondaryIpinfo.CountryFlagURL
	ipinfo.Gps = secondaryIpinfo.Location

	return ipinfo, nil
}

func GetIpinfoClient() Ipinfo {
	return ipClient
}
