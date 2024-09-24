package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/gcloud"
	"github.com/elc49/copod/Server/src/graph/model"
	"github.com/elc49/copod/Server/src/jwt"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/util"
)

func MobileSignin(signinController controllers.SigninController) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var res model.Signin
		log := logger.GetLogger()
		jwtService := jwt.GetJwtService()
		gcs := gcloud.GetGcloudService()
		var phone string
		var user *model.User
		var err error
		ctx := r.Context()
		avatarUrl := ""

		err = json.NewDecoder(r.Body).Decode(&phone)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		if config.Configuration != nil {
			avatarUrl, err = gcs.GenerateRandomAvatar(r.Context(), util.RandomStringByLength(5))
			if err != nil {
				log.WithError(err).Error("handler: ReadFromRemote()")
				http.Error(w, err.Error(), http.StatusBadRequest)
				return
			}
		}

		user, err = signinController.GetUserByPhone(ctx, phone)
		if user == nil && err == nil {
			user, err = signinController.CreateUserByPhone(ctx, phone, avatarUrl)
			if err != nil {
				http.Error(w, err.Error(), http.StatusBadRequest)
				return
			}
		} else if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		copodJwt, err := jwtService.Sign(jwt.NewPayload(user.ID.String(), jwtService.GetExpiry()))
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}
		res.Token = copodJwt
		res.UserID = user.ID
		res.HasFarmingRights = user.HasFarmingRights
		res.HasPosterRights = user.HasPosterRights

		if err := writeJSON(w, res, http.StatusOK); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
	})
}
