// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0

package db

import (
	"github.com/google/uuid"
)

type Session struct {
	ID        uuid.UUID   `json:"id"`
	Ip        string      `json:"ip"`
	UserID    uuid.UUID   `json:"user_id"`
	Expires   interface{} `json:"expires"`
	CreatedAt interface{} `json:"created_at"`
	UpdatedAt interface{} `json:"updated_at"`
}

type User struct {
	ID        uuid.UUID   `json:"id"`
	Phone     string      `json:"phone"`
	CreatedAt interface{} `json:"created_at"`
	UpdatedAt interface{} `json:"updated_at"`
	DeletedAt interface{} `json:"deleted_at"`
}
