package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/jwt"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
)

func RefreshToken(signinController controllers.SigninController) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ctx := r.Context()
		var res model.Signin
		jwtService := jwt.GetJwtService()
		log := logger.GetLogger()
		refreshToken := r.Header.Get("Refresh-Token")

		user, err := signinController.GetUserByID(ctx, graph.StringToUUID(refreshToken))
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

		result, err := json.Marshal(res)
		if err != nil {
			log.WithError(err).Error("handlers: json.Marshal() refresh token response")
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusCreated)
		w.Write(result)
	})
}
