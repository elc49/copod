package util

import (
	"crypto/rand"
	"encoding/json"
	"log"
	"math/big"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/nominatim"
	"github.com/google/uuid"
	"github.com/sirupsen/logrus"
)

func RandomStringByLength(length int) string {
	b := ""
	id, err := uuid.NewUUID()
	if err != nil {
		log.Fatalln(err)
	}

	for i := 0; i <= length; i++ {
		randInt, err := rand.Int(rand.Reader, big.NewInt(int64(length)))
		if err != nil {
			log.Fatalln(err)
			break
		}
		b += string(id.String()[randInt.Int64()])
	}

	return b
}

type point struct {
	Type        string    `json:"type"`
	Coordinates []float64 `json:"coordinates"`
}

func ParsePostgisLocation(p interface{}) *model.Address {
	var location *point

	if p != nil {
		json.Unmarshal([]byte((p).(string)), &location)

		lat := &location.Coordinates[1]
		lng := &location.Coordinates[0]
		address, err := nominatim.ReverseGeocode(model.Gps{Lat: *lat, Lng: *lng})
		if err != nil {
			logrus.WithError(err).Errorf("util: ReverseGeocode")
			return nil
		}
		return address
	} else {
		return nil
	}
}
