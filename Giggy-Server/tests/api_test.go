package tests

import (
	"bytes"
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/elc49/giggy-monorepo/Giggy-Server/handlers"
	"github.com/stretchr/testify/assert"
)

func Test_API(t *testing.T) {
	ctx := context.Background()
	defer func() {
		queries.ClearTestUsers(ctx)
	}()

	t.Run("mobile_signin_handler", func(t *testing.T) {
		payload, err := json.Marshal(phone)
		assert.Nil(t, err)

		req := httptest.NewRequest("POST", "http://example.com/signin", bytes.NewBuffer(payload))
		w := httptest.NewRecorder()
		handlers.MobileSignin(signinController()).ServeHTTP(w, req)

		res := w.Result()
		assert.Equal(t, res.Header.Get("Content-Type"), "application/json")
		assert.Equal(t, res.StatusCode, http.StatusOK)

		var result struct {
			Token string `json:"token"`
		}
		err = json.NewDecoder(res.Body).Decode(&result)
		assert.Nil(t, err)
		assert.True(t, len(result.Token) > 1, "should have jsonwebtoken string")
	})
}
