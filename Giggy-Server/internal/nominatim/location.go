package nominatim

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"strings"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
)

const nominatimApi = "https://nominatim.openstreetmap.org"

func ReverseGeocode(coords model.Gps) (*model.Address, error) {
	log := logger.GetLogger()
	var result struct {
		Lat     string `json:"lat"`
		Lng     string `json:"lon"`
		Address struct {
			Town        string `json:"town"`
			County      string `json:"county,omitempty"`
			State       string `json:"state"`
			CountryCode string `json:"country_code"`
		} `json:"address"`
	}
	address := new(model.Address)
	api := fmt.Sprintf("%s/reverse?format=jsonv2&lat=%f&lon=%f", nominatimApi, coords.Lat, coords.Lng)
	req, err := http.NewRequest("GET", api, nil)
	if err != nil {
		log.WithError(err).Errorf("nominatim: new http request")
		return nil, err
	}
	req.Header.Add("User-Agent", "giggy-api@v1.0") // One todo to avoid being rate-limited by nominatim admins

	c := &http.Client{}
	res, err := c.Do(req)
	if err != nil {
		log.WithError(err).Errorf("nominatim: geocode client response")
		return nil, err
	}
	defer res.Body.Close()

	if err := json.NewDecoder(res.Body).Decode(&result); err != nil {
		log.WithError(err).Errorf("nominatim: jwt.NewDecoder")
		return nil, err
	}

	address.AddressString = fmt.Sprintf("%s, %s, %s", result.Address.County, result.Address.State, strings.ToUpper(result.Address.CountryCode))

	lat, err := strconv.ParseFloat(result.Lat, 64)
	if err != nil {
		log.WithError(err).Error("nominatim: strconv.ParseFloat")
		return nil, err
	}

	lng, err := strconv.ParseFloat(result.Lng, 64)
	if err != nil {
		log.WithError(err).Errorf("nominatim: strconv.ParseFloat")
		return nil, err
	}
	address.Coords = &model.Gps{Lat: lat, Lng: lng}

	return address, nil
}
