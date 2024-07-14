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
  product, image, volume, unit, harvest_date, tag, price_per_unit, farm_id, location
) VALUES (
  $1, $2, $3, $4, $5, $6, $7, $8, $9
)
RETURNING id, product, image, volume, unit, price_per_unit, location, harvest_date, tag, farm_id, created_at, updated_at
`

type CreateFarmMarketParams struct {
	Product      string       `json:"product"`
	Image        string       `json:"image"`
	Volume       int32        `json:"volume"`
	Unit         string       `json:"unit"`
	HarvestDate  sql.NullTime `json:"harvest_date"`
	Tag          string       `json:"tag"`
	PricePerUnit int32        `json:"price_per_unit"`
	FarmID       uuid.UUID    `json:"farm_id"`
	Location     interface{}  `json:"location"`
}

func (q *Queries) CreateFarmMarket(ctx context.Context, arg CreateFarmMarketParams) (Market, error) {
	row := q.db.QueryRowContext(ctx, createFarmMarket,
		arg.Product,
		arg.Image,
		arg.Volume,
		arg.Unit,
		arg.HarvestDate,
		arg.Tag,
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
		&i.Unit,
		&i.PricePerUnit,
		&i.Location,
		&i.HarvestDate,
		&i.Tag,
		&i.FarmID,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getLocalizedMarkets = `-- name: GetLocalizedMarkets :many
SELECT id, product, image, price_per_unit, unit, farm_id, location, created_at, updated_at FROM markets
WHERE ST_DWithin(location, $1::geography, $2)
`

type GetLocalizedMarketsParams struct {
	Point  interface{} `json:"point"`
	Radius interface{} `json:"radius"`
}

type GetLocalizedMarketsRow struct {
	ID           uuid.UUID   `json:"id"`
	Product      string      `json:"product"`
	Image        string      `json:"image"`
	PricePerUnit int32       `json:"price_per_unit"`
	Unit         string      `json:"unit"`
	FarmID       uuid.UUID   `json:"farm_id"`
	Location     interface{} `json:"location"`
	CreatedAt    time.Time   `json:"created_at"`
	UpdatedAt    time.Time   `json:"updated_at"`
}

func (q *Queries) GetLocalizedMarkets(ctx context.Context, arg GetLocalizedMarketsParams) ([]GetLocalizedMarketsRow, error) {
	rows, err := q.db.QueryContext(ctx, getLocalizedMarkets, arg.Point, arg.Radius)
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
			&i.PricePerUnit,
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
SELECT id, product, image, volume, unit, farm_id, tag, price_per_unit, created_at, updated_at FROM markets
WHERE id = $1
`

type GetMarketByIDRow struct {
	ID           uuid.UUID `json:"id"`
	Product      string    `json:"product"`
	Image        string    `json:"image"`
	Volume       int32     `json:"volume"`
	Unit         string    `json:"unit"`
	FarmID       uuid.UUID `json:"farm_id"`
	Tag          string    `json:"tag"`
	PricePerUnit int32     `json:"price_per_unit"`
	CreatedAt    time.Time `json:"created_at"`
	UpdatedAt    time.Time `json:"updated_at"`
}

func (q *Queries) GetMarketByID(ctx context.Context, id uuid.UUID) (GetMarketByIDRow, error) {
	row := q.db.QueryRowContext(ctx, getMarketByID, id)
	var i GetMarketByIDRow
	err := row.Scan(
		&i.ID,
		&i.Product,
		&i.Image,
		&i.Volume,
		&i.Unit,
		&i.FarmID,
		&i.Tag,
		&i.PricePerUnit,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getMarketsBelongingToFarm = `-- name: GetMarketsBelongingToFarm :many
SELECT id, product, image, volume, unit, farm_id, price_per_unit, tag, harvest_date, created_at, updated_at FROM markets
WHERE farm_id = $1
`

type GetMarketsBelongingToFarmRow struct {
	ID           uuid.UUID    `json:"id"`
	Product      string       `json:"product"`
	Image        string       `json:"image"`
	Volume       int32        `json:"volume"`
	Unit         string       `json:"unit"`
	FarmID       uuid.UUID    `json:"farm_id"`
	PricePerUnit int32        `json:"price_per_unit"`
	Tag          string       `json:"tag"`
	HarvestDate  sql.NullTime `json:"harvest_date"`
	CreatedAt    time.Time    `json:"created_at"`
	UpdatedAt    time.Time    `json:"updated_at"`
}

func (q *Queries) GetMarketsBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]GetMarketsBelongingToFarmRow, error) {
	rows, err := q.db.QueryContext(ctx, getMarketsBelongingToFarm, farmID)
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
			&i.Unit,
			&i.FarmID,
			&i.PricePerUnit,
			&i.Tag,
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
