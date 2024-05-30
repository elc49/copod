package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/ip"
)

func Ip() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ipinfo, err := ip.GetIpinfoClient().GetIpinfo(r.RemoteAddr)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		res, err := json.Marshal(ipinfo)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write(res)
	})
}
