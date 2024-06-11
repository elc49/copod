package handlers

import (
	"encoding/json"
	"errors"
	"net/http"

	"github.com/elc49/giggy-monorepo/Giggy-Server/internal/gcloud"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
)

var (
	FileTooLargeErr  = errors.New("handlers: file too large")
	NoFileContentErr = errors.New("handlers: no file content")
	maxSize          = int64(6000000)
)

func PostUploader() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		uploader := gcloud.GetGcloudService()
		log := logger.GetLogger()

		err := r.ParseMultipartForm(maxSize)
		if err != nil {
			http.Error(w, FileTooLargeErr.Error(), http.StatusBadRequest)
			return
		}

		file, fileHeader, err := r.FormFile("file")
		if err != nil {
			http.Error(w, NoFileContentErr.Error(), http.StatusBadRequest)
			return
		}
		defer file.Close()

		url, err := uploader.UploadPostImage(r.Context(), file, fileHeader)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		res, err := json.Marshal(struct {
			ImageUri string `json:"image_uri"`
		}{ImageUri: url})
		if err != nil {
			log.WithError(err).Error("handlers: json.Marshal() response")
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusCreated)
		w.Write(res)
	})
}
