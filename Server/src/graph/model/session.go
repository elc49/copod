package model

import (
	"github.com/google/uuid"
)

type Signin struct {
	UserID           uuid.UUID `json:"user_id"`
	Token            string    `json:"token"`
	HasFarmingRights bool      `json:"has_farming_rights"`
	HasPosterRights  bool      `json:"has_poster_rights"`
}
