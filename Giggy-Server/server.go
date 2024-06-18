package main

import (
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/config"
	"github.com/elc49/giggy-monorepo/Giggy-Server/server"
)

func main() {
	s := server.New()
	s.MountHandlers()
	http.ListenAndServe("0.0.0.0:"+config.Configuration.Server.Port, s.Router)
}
