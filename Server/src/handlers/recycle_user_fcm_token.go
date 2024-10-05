package handlers

import (
	"database/sql"
	"encoding/json"
	"net/http"

	"github.com/elc49/copod/Server/src/controllers"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/elc49/copod/Server/src/util"
)

func RecycleUserFCMToken() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		ctx := r.Context()
		log := logger.GetLogger()
		usv := controllers.GetUserController()
		var req struct {
			TokenID string `json:"tokenId"`
			UserID  string `json:"userId"`
		}

		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			log.WithError(err).Errorf("handlers: decode RecycleUserFCMToken req")
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		userId := util.StringToUUID(req.UserID)
		args := db.SetUserNotificationTrackingIDParams{
			NotificationTrackingID: sql.NullString{String: req.TokenID, Valid: true},
			ID:                     userId,
		}
		usv.SetUserNotificationTrackingID(ctx, args)

		writeJSON(w, struct{}{}, http.StatusOK)
	})
}
