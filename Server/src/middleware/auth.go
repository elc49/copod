package middleware

import (
	"context"
	"errors"
	"net/http"
	"strings"

	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/jwt"
	"github.com/elc49/copod/Server/src/util"
	jsonwebtoken "github.com/golang-jwt/jwt/v5"
)

var ErrInvalidAuthHeader = errors.New("middleware: invalid token header")

func Auth(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var id string
		ctx := r.Context()

		token, err := validateAuthHeader(r)
		if err != nil {
			http.Error(w, err.Error(), http.StatusUnauthorized)
			return
		}

		if claims, ok := token.Claims.(*jwt.Payload); ok && token.Valid {
			id = claims.ID
		}

		u, err := controllers.GetSigninController().GetUserByID(r.Context(), util.StringToUUID(id))
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		ctx = context.WithValue(ctx, "userId", id)
		ctx = context.WithValue(ctx, "ip", r.RemoteAddr)
		ctx = context.WithValue(ctx, "notification_tracking_id", u.NotificationTrackingID)
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

func validateAuthHeader(r *http.Request) (*jsonwebtoken.Token, error) {
	jwtService := jwt.GetJwtService()

	authHeader := strings.SplitN(r.Header.Get("Authorization"), " ", 2)
	if len(authHeader) != 2 || authHeader[0] != "Bearer" {
		return nil, ErrInvalidAuthHeader
	}

	tokenString := authHeader[1]
	token, err := jwtService.Verify(tokenString)
	if err != nil {
		return nil, err
	}

	return token, nil
}
