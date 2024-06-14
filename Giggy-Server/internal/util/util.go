package util

import (
	"crypto/rand"
	"log"
	"math/big"

	"github.com/google/uuid"
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
