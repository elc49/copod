package paystack

import (
	"encoding/json"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/sirupsen/logrus"
)

func Paystack() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		paystackRes := &model.ChargeMpesaPhoneCallbackRes{}
		body := r.Context().Value("body").([]byte)

		mErr := json.Unmarshal(body, &paystackRes)
		if mErr != nil {
			logrus.WithError(mErr).Error("paystack webhook: json.Unmarshal()")
			http.Error(w, mErr.Error(), http.StatusInternalServerError)
			return
		}

		w.WriteHeader(http.StatusOK)
	})
}
