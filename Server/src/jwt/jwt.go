package jwt

import (
	"errors"
	"time"

	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/logger"
	jsonwebtoken "github.com/golang-jwt/jwt/v5"
	"github.com/sirupsen/logrus"
)

var (
	ErrInvalidSigningAlg = errors.New("jwt: invalid signing algorithm")
	jwt                  Jwt
)

type Jwt interface {
	Sign(claims jsonwebtoken.Claims) (string, error)
	Verify(token string) (*jsonwebtoken.Token, error)
	GetExpiry() time.Duration
}

type jwtService struct {
	secret  string
	expires time.Duration
	log     *logrus.Logger
}

func New(opt config.Jwt) {
	jwt = jwtService{opt.Secret, opt.Expires, logger.GetLogger()}
}

func GetJwtService() Jwt { return jwt }

func (j jwtService) getSecret() []byte { return []byte(j.secret) }

func (j jwtService) GetExpiry() time.Duration { return j.expires }

func (j jwtService) Sign(claims jsonwebtoken.Claims) (string, error) {
	token, err := jsonwebtoken.NewWithClaims(jsonwebtoken.SigningMethodHS256, claims).SignedString(j.getSecret())
	if err != nil {
		j.log.WithError(err).Errorf("jwt: NewWithClaims")
		return "", err
	}

	return token, nil
}

func (j jwtService) Verify(token string) (*jsonwebtoken.Token, error) {
	keyFunc := func(tkn *jsonwebtoken.Token) (interface{}, error) {
		if _, ok := tkn.Method.(*jsonwebtoken.SigningMethodHMAC); !ok {
			return nil, ErrInvalidSigningAlg
		}

		return []byte(j.secret), nil
	}

	tkn, err := jsonwebtoken.ParseWithClaims(token, &Payload{}, keyFunc)
	if err != nil {
		return nil, err
	}

	return tkn, nil
}
