package handlers

import (
	"html/template"
	"net/http"
	"path/filepath"
)

func Privacy() http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		templatesDir, err := filepath.Abs("static")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		t, err := template.ParseFiles(filepath.Join(templatesDir, "privacy.html"))
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if err := t.Execute(w, nil); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
	})
}
