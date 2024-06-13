// Code generated by github.com/99designs/gqlgen, DO NOT EDIT.

package model

import (
	"time"

	"github.com/google/uuid"
)

type GpsInput struct {
	Lat float64 `json:"lat"`
	Lng float64 `json:"lng"`
}

type Mutation struct {
}

type NewPostInput struct {
	Text     string    `json:"text"`
	Image    string    `json:"image"`
	Tags     []string  `json:"tags"`
	UserID   uuid.UUID `json:"userId"`
	Location *GpsInput `json:"location"`
}

type Post struct {
	ID        uuid.UUID `json:"id"`
	Text      string    `json:"text"`
	Image     string    `json:"image"`
	Tags      []string  `json:"tags"`
	UserID    uuid.UUID `json:"userId"`
	User      *User     `json:"user"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type Query struct {
}
