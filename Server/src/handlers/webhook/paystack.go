package paystack

import (
	"context"
	"encoding/json"
	"io"
	"net/http"

	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/paystack"
	"github.com/sirupsen/logrus"
)

func Paystack() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		pS := paystack.GetPaystackService()
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

		go func() {
			ctx := context.Background()
			psErr := pS.ReconcileMpesaChargeCallback(ctx, *paystackRes)
			if psErr != nil {
				return
			}
		}()

		w.WriteHeader(http.StatusOK)
	})
}
