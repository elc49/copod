package paystack

import (
	"encoding/json"
	"io"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/sirupsen/logrus"
)

func Paystack() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		paystackRes := &model.ChargeMpesaPhoneCallbackRes{}
		body, err := io.ReadAll(r.Body)
		if err != nil {
			logrus.WithError(err).Errorf("paystack webhook: io.ReadAll(r.Body)")
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		defer r.Body.Close()

		mErr := json.Unmarshal(body, &paystackRes)
		if mErr != nil {
			logrus.WithError(mErr).Error("paystack webhook: json.Unmarshal()")
			http.Error(w, mErr.Error(), http.StatusInternalServerError)
			return
		}

		w.WriteHeader(http.StatusOK)
	})
}
