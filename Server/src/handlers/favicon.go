package handlers

import (
	"net/http"
	"path/filepath"
)

func Favicon() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		templatesDir, err := filepath.Abs("static")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		http.ServeFile(w, r, filepath.Join(templatesDir, "favicon.ico"))
	})
}
