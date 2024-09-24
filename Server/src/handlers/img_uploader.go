package handlers

import (
	"errors"
	"net/http"

	"github.com/elc49/copod/Server/src/gcloud"
	"github.com/elc49/copod/Server/src/logger"
)

var (
	FileTooLargeErr  = errors.New("handlers: file too large")
	NoFileContentErr = errors.New("handlers: no file content")
	maxSize          = int64(6000000)
)

func ImageUploader() http.Handler {
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
			log.WithError(err).Error("handlers: uploader.ImageUploader()")
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if err := writeJSON(w, struct {
			ImageUri string `json:"image_uri"`
		}{ImageUri: url}, http.StatusCreated); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
	})
}
