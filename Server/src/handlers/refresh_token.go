package handlers

import (
	"net/http"

	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/jwt"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/util"
)

func RefreshToken(signinController controllers.SigninController) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ctx := r.Context()
		var res model.Signin
		jwtService := jwt.GetJwtService()
		log := logger.GetLogger()
		refreshToken := r.Header.Get("Refresh-Token")

		user, err := signinController.GetUserByID(ctx, util.StringToUUID(refreshToken))
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		token, err := jwtService.Sign(jwt.NewPayload(user.ID.String(), jwtService.GetExpiry()))
		if err != nil {
			log.WithError(err).Error("handlers: RefreshToken() jwt signin")
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		res.Token = token
		res.UserID = user.ID
		res.HasFarmingRights = user.HasFarmingRights
		res.HasPosterRights = user.HasPosterRights

		if err := writeJSON(w, res, http.StatusCreated); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
	})
}
