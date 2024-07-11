// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0

package db

import (
	"database/sql"
	"time"

	"github.com/google/uuid"
)

type Cart struct {
	ID        uuid.UUID `json:"id"`
	Volume    int32     `json:"volume"`
	MarketID  uuid.UUID `json:"market_id"`
	FarmID    uuid.UUID `json:"farm_id"`
	UserID    uuid.UUID `json:"user_id"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type Farm struct {
	ID        uuid.UUID    `json:"id"`
	Name      string       `json:"name"`
	Thumbnail string       `json:"thumbnail"`
	UserID    uuid.UUID    `json:"user_id"`
	CreatedAt time.Time    `json:"created_at"`
	UpdatedAt time.Time    `json:"updated_at"`
	DeletedAt sql.NullTime `json:"deleted_at"`
}

type Market struct {
	ID           uuid.UUID    `json:"id"`
	Product      string       `json:"product"`
	Image        string       `json:"image"`
	Volume       int32        `json:"volume"`
	Unit         string       `json:"unit"`
	PricePerUnit int32        `json:"price_per_unit"`
	Location     interface{}  `json:"location"`
	HarvestDate  sql.NullTime `json:"harvest_date"`
	Tag          string       `json:"tag"`
	FarmID       uuid.UUID    `json:"farm_id"`
	CreatedAt    time.Time    `json:"created_at"`
	UpdatedAt    time.Time    `json:"updated_at"`
}

type Order struct {
	ID         uuid.UUID `json:"id"`
	Volume     int32     `json:"volume"`
	ToBePaid   int32     `json:"to_be_paid"`
	CustomerID uuid.UUID `json:"customer_id"`
	MarketID   uuid.UUID `json:"market_id"`
	FarmID     uuid.UUID `json:"farm_id"`
	CreatedAt  time.Time `json:"created_at"`
	UpdatedAt  time.Time `json:"updated_at"`
}

type Payment struct {
	ID          uuid.UUID      `json:"id"`
	Customer    string         `json:"customer"`
	Amount      int32          `json:"amount"`
	Reason      string         `json:"reason"`
	Status      string         `json:"status"`
	ReferenceID sql.NullString `json:"reference_id"`
	UserID      uuid.UUID      `json:"user_id"`
	OrderID     uuid.NullUUID  `json:"order_id"`
	MarketID    uuid.NullUUID  `json:"market_id"`
	FarmID      uuid.NullUUID  `json:"farm_id"`
	CreatedAt   time.Time      `json:"created_at"`
	UpdatedAt   time.Time      `json:"updated_at"`
}

type Post struct {
	ID        uuid.UUID   `json:"id"`
	Text      string      `json:"text"`
	Image     string      `json:"image"`
	Tags      []string    `json:"tags"`
	Location  interface{} `json:"location"`
	UserID    uuid.UUID   `json:"user_id"`
	CreatedAt time.Time   `json:"created_at"`
	UpdatedAt time.Time   `json:"updated_at"`
}

type User struct {
	ID               uuid.UUID      `json:"id"`
	Phone            string         `json:"phone"`
	Username         sql.NullString `json:"username"`
	Avatar           string         `json:"avatar"`
	HasFarmingRights bool           `json:"has_farming_rights"`
	HasPosterRights  bool           `json:"has_poster_rights"`
	CreatedAt        time.Time      `json:"created_at"`
	UpdatedAt        time.Time      `json:"updated_at"`
	DeletedAt        sql.NullTime   `json:"deleted_at"`
}
