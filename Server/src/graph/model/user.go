package model

import (
	"time"

	"github.com/google/uuid"
)

type User struct {
	ID               uuid.UUID  `json:"id"`
	Phone            string     `json:"phone"`
	Avatar           string     `json:"avatar"`
	Username         string     `json:"username"`
	HasFarmingRights bool       `json:"has_farming_rights"`
	HasPosterRights  bool       `json:"has_poster_rights"`
	CreatedAt        time.Time  `json:"created_at"`
	UpdatedAt        time.Time  `json:"updated_at"`
	DeletedAt        *time.Time `json:"deleted_at"`
}
