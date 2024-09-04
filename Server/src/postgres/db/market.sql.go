// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0
// source: market.sql

package db

import (
	"context"
	"database/sql"
	"time"

	"github.com/google/uuid"
)

const clearTestMarkets = `-- name: ClearTestMarkets :exec
DELETE FROM markets
`

func (q *Queries) ClearTestMarkets(ctx context.Context) error {
	_, err := q.db.ExecContext(ctx, clearTestMarkets)
	return err
}

const createFarmMarket = `-- name: CreateFarmMarket :one
INSERT INTO markets (
  product, details, type, image, volume, running_volume, unit, harvest_date, price_per_unit, farm_id, location
) VALUES (
  $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11
)
RETURNING id, product, image, volume, running_volume, type, status, unit, details, price_per_unit, location, harvest_date, farm_id, created_at, updated_at
`

type CreateFarmMarketParams struct {
	Product       string       `json:"product"`
	Details       string       `json:"details"`
	Type          string       `json:"type"`
	Image         string       `json:"image"`
	Volume        int32        `json:"volume"`
	RunningVolume int32        `json:"running_volume"`
	Unit          string       `json:"unit"`
	HarvestDate   sql.NullTime `json:"harvest_date"`
	PricePerUnit  int32        `json:"price_per_unit"`
	FarmID        uuid.UUID    `json:"farm_id"`
	Location      interface{}  `json:"location"`
}

func (q *Queries) CreateFarmMarket(ctx context.Context, arg CreateFarmMarketParams) (Market, error) {
	row := q.db.QueryRowContext(ctx, createFarmMarket,
		arg.Product,
		arg.Details,
		arg.Type,
		arg.Image,
		arg.Volume,
		arg.RunningVolume,
		arg.Unit,
		arg.HarvestDate,
		arg.PricePerUnit,
		arg.FarmID,
		arg.Location,
	)
	var i Market
	err := row.Scan(
		&i.ID,
		&i.Product,
		&i.Image,
		&i.Volume,
		&i.RunningVolume,
		&i.Type,
		&i.Status,
		&i.Unit,
		&i.Details,
		&i.PricePerUnit,
		&i.Location,
		&i.HarvestDate,
		&i.FarmID,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getFarmOwnerID = `-- name: GetFarmOwnerID :one
SELECT user_id FROM farms
WHERE id = $1
`

func (q *Queries) GetFarmOwnerID(ctx context.Context, id uuid.UUID) (uuid.UUID, error) {
	row := q.db.QueryRowContext(ctx, getFarmOwnerID, id)
	var user_id uuid.UUID
	err := row.Scan(&user_id)
	return user_id, err
}

const getLocalizedMarkets = `-- name: GetLocalizedMarkets :many
SELECT id, product, image, details, price_per_unit, status, running_volume, volume, unit, farm_id, location, created_at, updated_at FROM markets
WHERE ST_DWithin(location, $1::geography, $2) AND running_volume > 0 AND type = $3
`

type GetLocalizedMarketsParams struct {
	Point  interface{} `json:"point"`
	Radius interface{} `json:"radius"`
	Type   string      `json:"type"`
}

type GetLocalizedMarketsRow struct {
	ID            uuid.UUID   `json:"id"`
	Product       string      `json:"product"`
	Image         string      `json:"image"`
	Details       string      `json:"details"`
	PricePerUnit  int32       `json:"price_per_unit"`
	Status        string      `json:"status"`
	RunningVolume int32       `json:"running_volume"`
	Volume        int32       `json:"volume"`
	Unit          string      `json:"unit"`
	FarmID        uuid.UUID   `json:"farm_id"`
	Location      interface{} `json:"location"`
	CreatedAt     time.Time   `json:"created_at"`
	UpdatedAt     time.Time   `json:"updated_at"`
}

func (q *Queries) GetLocalizedMarkets(ctx context.Context, arg GetLocalizedMarketsParams) ([]GetLocalizedMarketsRow, error) {
	rows, err := q.db.QueryContext(ctx, getLocalizedMarkets, arg.Point, arg.Radius, arg.Type)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []GetLocalizedMarketsRow{}
	for rows.Next() {
		var i GetLocalizedMarketsRow
		if err := rows.Scan(
			&i.ID,
			&i.Product,
			&i.Image,
			&i.Details,
			&i.PricePerUnit,
			&i.Status,
			&i.RunningVolume,
			&i.Volume,
			&i.Unit,
			&i.FarmID,
			&i.Location,
			&i.CreatedAt,
			&i.UpdatedAt,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getMarketByID = `-- name: GetMarketByID :one
SELECT id, product, type, details, image, volume, running_volume, status, unit, farm_id, price_per_unit, created_at, updated_at FROM markets
WHERE id = $1
`

type GetMarketByIDRow struct {
	ID            uuid.UUID `json:"id"`
	Product       string    `json:"product"`
	Type          string    `json:"type"`
	Details       string    `json:"details"`
	Image         string    `json:"image"`
	Volume        int32     `json:"volume"`
	RunningVolume int32     `json:"running_volume"`
	Status        string    `json:"status"`
	Unit          string    `json:"unit"`
	FarmID        uuid.UUID `json:"farm_id"`
	PricePerUnit  int32     `json:"price_per_unit"`
	CreatedAt     time.Time `json:"created_at"`
	UpdatedAt     time.Time `json:"updated_at"`
}

func (q *Queries) GetMarketByID(ctx context.Context, id uuid.UUID) (GetMarketByIDRow, error) {
	row := q.db.QueryRowContext(ctx, getMarketByID, id)
	var i GetMarketByIDRow
	err := row.Scan(
		&i.ID,
		&i.Product,
		&i.Type,
		&i.Details,
		&i.Image,
		&i.Volume,
		&i.RunningVolume,
		&i.Status,
		&i.Unit,
		&i.FarmID,
		&i.PricePerUnit,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getMarketsBelongingToFarm = `-- name: GetMarketsBelongingToFarm :many
SELECT id, product, image, volume, type, running_volume, unit, farm_id, status, price_per_unit, harvest_date, created_at, updated_at FROM markets
WHERE farm_id = $1 AND type = $2
`

type GetMarketsBelongingToFarmParams struct {
	FarmID uuid.UUID `json:"farm_id"`
	Type   string    `json:"type"`
}

type GetMarketsBelongingToFarmRow struct {
	ID            uuid.UUID    `json:"id"`
	Product       string       `json:"product"`
	Image         string       `json:"image"`
	Volume        int32        `json:"volume"`
	Type          string       `json:"type"`
	RunningVolume int32        `json:"running_volume"`
	Unit          string       `json:"unit"`
	FarmID        uuid.UUID    `json:"farm_id"`
	Status        string       `json:"status"`
	PricePerUnit  int32        `json:"price_per_unit"`
	HarvestDate   sql.NullTime `json:"harvest_date"`
	CreatedAt     time.Time    `json:"created_at"`
	UpdatedAt     time.Time    `json:"updated_at"`
}

func (q *Queries) GetMarketsBelongingToFarm(ctx context.Context, arg GetMarketsBelongingToFarmParams) ([]GetMarketsBelongingToFarmRow, error) {
	rows, err := q.db.QueryContext(ctx, getMarketsBelongingToFarm, arg.FarmID, arg.Type)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []GetMarketsBelongingToFarmRow{}
	for rows.Next() {
		var i GetMarketsBelongingToFarmRow
		if err := rows.Scan(
			&i.ID,
			&i.Product,
			&i.Image,
			&i.Volume,
			&i.Type,
			&i.RunningVolume,
			&i.Unit,
			&i.FarmID,
			&i.Status,
			&i.PricePerUnit,
			&i.HarvestDate,
			&i.CreatedAt,
			&i.UpdatedAt,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const setMarketStatus = `-- name: SetMarketStatus :one
UPDATE markets SET status = $1
WHERE id = $2
RETURNING id, product, image, volume, running_volume, type, status, unit, details, price_per_unit, location, harvest_date, farm_id, created_at, updated_at
`

type SetMarketStatusParams struct {
	Status string    `json:"status"`
	ID     uuid.UUID `json:"id"`
}

func (q *Queries) SetMarketStatus(ctx context.Context, arg SetMarketStatusParams) (Market, error) {
	row := q.db.QueryRowContext(ctx, setMarketStatus, arg.Status, arg.ID)
	var i Market
	err := row.Scan(
		&i.ID,
		&i.Product,
		&i.Image,
		&i.Volume,
		&i.RunningVolume,
		&i.Type,
		&i.Status,
		&i.Unit,
		&i.Details,
		&i.PricePerUnit,
		&i.Location,
		&i.HarvestDate,
		&i.FarmID,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const updateMarketVolume = `-- name: UpdateMarketVolume :one
UPDATE markets SET running_volume = $1
WHERE id = $2
RETURNING id, product, image, volume, running_volume, type, status, unit, details, price_per_unit, location, harvest_date, farm_id, created_at, updated_at
`

type UpdateMarketVolumeParams struct {
	RunningVolume int32     `json:"running_volume"`
	ID            uuid.UUID `json:"id"`
}

func (q *Queries) UpdateMarketVolume(ctx context.Context, arg UpdateMarketVolumeParams) (Market, error) {
	row := q.db.QueryRowContext(ctx, updateMarketVolume, arg.RunningVolume, arg.ID)
	var i Market
	err := row.Scan(
		&i.ID,
		&i.Product,
		&i.Image,
		&i.Volume,
		&i.RunningVolume,
		&i.Type,
		&i.Status,
		&i.Unit,
		&i.Details,
		&i.PricePerUnit,
		&i.Location,
		&i.HarvestDate,
		&i.FarmID,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}
