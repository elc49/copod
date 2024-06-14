package handlers

import (
	"encoding/json"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/graph/model"
	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/jwt"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
)

func MobileSignin(signinController controllers.SigninController) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		var res model.Signin
		log := logger.GetLogger()
		jwtService := jwt.GetJwtService()
		var phone string
		var user *model.User
		var err error
		ctx := r.Context()

		err = json.NewDecoder(r.Body).Decode(&phone)
		if err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		user, err = signinController.GetUserByPhone(ctx, phone)
		if user == nil && err == nil {
			user, err = signinController.CreateUserByPhone(ctx, phone)
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
