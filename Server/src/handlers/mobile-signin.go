package handlers

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/controllers"
	"github.com/elc49/vuno/Server/src/gcloud"
	"github.com/elc49/vuno/Server/src/graph/model"
	"github.com/elc49/vuno/Server/src/jwt"
	"github.com/elc49/vuno/Server/src/logger"
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
			avatarRes, err := http.Get(fmt.Sprintf("%savatar_%s", config.Configuration.RemoteAvatar, phone))
			if err != nil {
				log.WithError(err).Error("handlers: http.Get avatarRes")
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
			defer avatarRes.Body.Close()

			if avatarRes.StatusCode != http.StatusOK {
				log.WithError(err).Error("handlers: avatarRes not http.StatusOK")
			}

			avatarUrl, err = gcs.ReadFromRemote(r.Context(), avatarRes.Body)
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

		jwt, err := jwtService.Sign(jwt.NewPayload(user.ID.String(), jwtService.GetExpiry()))
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}
		res.Token = jwt
		res.UserID = user.ID
		res.HasFarmingRights = user.HasFarmingRights
		res.HasPosterRights = user.HasPosterRights

		result, err := json.Marshal(res)
		if err != nil {
			log.WithError(err).Error("handlers: json.Marshal() signin response")
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write(result)
	})
}
