package jwt

import (
	"time"

	jsonwebtoken "github.com/golang-jwt/jwt/v5"
)

type Payload struct {
	jsonwebtoken.RegisteredClaims
}

func NewPayload(id string, duration time.Duration) *Payload {
	return &Payload{
		jsonwebtoken.RegisteredClaims{
			Issuer:    "giggy",
			ExpiresAt: jsonwebtoken.NewNumericDate(time.Now().Add(duration)),
			IssuedAt:  jsonwebtoken.NewNumericDate(time.Now()),
			ID:        id,
		},
	}
}
