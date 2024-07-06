package middleware

import (
	"crypto/hmac"
	"crypto/sha512"
	"encoding/hex"
	"errors"
	"io"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/sirupsen/logrus"
)

func Paystack(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		paystackSignature := r.Header.Get("x-paystack-signature")

		hash := hmac.New(sha512.New, []byte(config.Configuration.Paystack.SecretKey))
		body, err := io.ReadAll(r.Body)
		if err != nil {
			logrus.WithError(err).Errorf("middleware: io.ReadAll(r.Body)")
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}
		hash.Write(body)
		expectedHmac := hex.EncodeToString(hash.Sum(nil))
		if expectedHmac != paystackSignature {
			http.Error(w, errors.New("paystack: invalid signature").Error(), http.StatusUnauthorized)
			return
		}

		next.ServeHTTP(w, r)
	})
}
