package nominatim

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
)

const nominatimApi = "https://nominatim.openstreetmap.org"

func ReverseGeocode(coords model.Gps) (*model.Address, error) {
	var result struct {
		Name        string `json:"name"`
		DisplayName string `json:"display_name"`
		Lat         string `json:"lat"`
		Lng         string `json:"lon"`
		Address     struct {
			Village string `json:"village,omitempty"`
			County  string `json:"state,omitempty"`
			Country string `json:"country,omitempty"`
			Region  string `json:"region,omitempty"`
		} `json:"address"`
	}
	address := new(model.Address)
	api := fmt.Sprintf("%s/reverse?format=jsonv2&lat=%f&lon=%f", nominatimApi, coords.Lat, coords.Lng)
	res, err := http.Get(api)
	if err != nil {
		return nil, err
	}
	defer res.Body.Close()

	if err := json.NewDecoder(res.Body).Decode(&result); err != nil {
		return nil, err
	}

	if result.Name == "" {
		address.AddressString = result.DisplayName
	} else {
		address.AddressString = result.Name
	}

	lat, err := strconv.ParseFloat(result.Lat, 64)
	if err != nil {
		return nil, err
	}

	lng, err := strconv.ParseFloat(result.Lng, 64)
	if err != nil {
		return nil, err
	}
	address.Coords = &model.Gps{Lat: lat, Lng: lng}

	return address, nil
}
