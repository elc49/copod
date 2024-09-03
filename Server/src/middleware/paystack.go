package middleware

import (
	"bytes"
	"crypto/hmac"
	"crypto/sha512"
	"encoding/hex"
	"errors"
	"io"
	"net/http"

	"github.com/elc49/copod/Server/src/config"
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

		// Because we're going to chain this middleware to paystack webhook
		// handler we create a copy of r.Body for the handler since
		// reading it here to handshake with paystack signature will pass
		// nil value after reading r.Body to the next handler
		r.Body = io.NopCloser(bytes.NewBuffer(body))
		next.ServeHTTP(w, r)
	})
}
