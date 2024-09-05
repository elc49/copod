//go:generate npm run build
package main

import (
	"embed"
	"github.com/sirupsen/logrus"
	"net/http"

	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/server"
)

//go:embed static
var static embed.FS

func main() {
	s := server.New()
	s.MountHandlers(static)
	err := http.ListenAndServe("0.0.0.0:"+config.Configuration.Server.Port, s.Router)
	if err != nil {
		logrus.WithError(err).Errorf("server: create new instance")
		return
	}
}
