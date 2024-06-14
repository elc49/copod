package model

import (
	"github.com/google/uuid"
)

type Signin struct {
	UserID uuid.UUID `json:"user_id"`
	Token  string    `json:"token"`
}
