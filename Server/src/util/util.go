package util

import (
	"crypto/rand"
	"encoding/json"
	"math/big"

	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/logger"
	"github.com/google/uuid"
)

var log = logger.GetLogger()

func RandomStringByLength(length int) string {
	log := logger.GetLogger()
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
		return &model.Address{Coords: &model.Gps{Lat: *lat, Lng: *lng}}
	} else {
		return nil
	}
}

func StringToUUID(id string) uuid.UUID {
	uid, err := uuid.Parse(id)
	if err != nil {
		log.WithError(err).Error("util: StringToUUID()")
	}

	return uid
}
