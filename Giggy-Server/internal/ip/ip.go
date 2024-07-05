package ip

import (
	"encoding/json"
	"fmt"
	"io"
	"net"
	"net/http"
	"time"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/ipinfo/go/v2/ipinfo"
	"github.com/ipinfo/go/v2/ipinfo/cache"
)

type Ipinfo interface {
	GetIpinfo(ip string) (*model.Ipinfo, error)
}

var (
	ipClient Ipinfo
)

type ipC struct {
	client *ipinfo.Client
}

func NewIpinfoClient() {
	client := ipinfo.NewClient(
		nil,
		ipinfo.NewCache(
			cache.NewInMemory().WithExpiration(time.Hour*24),
		),
		config.Configuration.Ipinfo.ApiKey,
	)

	ipClient = &ipC{client}
}

func (ipc ipC) GetIpinfo(ip string) (*model.Ipinfo, error) {
	log := logger.GetLogger()
	var ipinfo *model.Ipinfo

	ipApiClient := http.Client{}
	req, err := http.NewRequest("GET", fmt.Sprintf("https://ipapi.co/%s/json/", ip), nil)
	req.Header.Set("User-Agent", "ipapi.co/#go-v1.5")
	if err != nil {
		log.WithError(err).Error("ip: htttp.NewRequest")
		return nil, err
	}

	res, err := ipApiClient.Do(req)
	if err != nil {
		log.WithError(err).Error("ip: ipApiClient.Do")
		return nil, err
	}

	defer res.Body.Close()
	body, err := io.ReadAll(res.Body)
	if err != nil {
		log.WithError(err).Error("ip: res.Body")
		return nil, err
	}

	err = json.Unmarshal(body, &ipinfo)
	if err != nil {
		log.WithError(err).Error("ip: json.Unmarshal")
		return nil, err
	}

	secondaryIpinfo, err := ipc.client.GetIPInfo(net.ParseIP(ip))
	if err != nil {
		log.WithError(err).Error("ip: ipc.GetIPInfo")
		return nil, err
	}
	ipinfo.CountryFlagURL = secondaryIpinfo.CountryFlagURL
	ipinfo.Gps = secondaryIpinfo.Location
	ipinfo.FarmingRightsFee = config.Configuration.Fees.FarmingRights
	ipinfo.PosterRightsFee = config.Configuration.Fees.PosterRights

	return ipinfo, nil
}

func GetIpinfoClient() Ipinfo {
	return ipClient
}
