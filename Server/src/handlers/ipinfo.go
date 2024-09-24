package handlers

import (
	"net/http"

	"github.com/elc49/copod/Server/src/ip"
)

func Ip() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ipinfo, err := ip.GetIpinfoClient().GetIpinfo(r.RemoteAddr)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		err = writeJSON(w, ipinfo, http.StatusOK)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
	})
}
