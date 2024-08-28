// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0
// source: farm.sql

package db

import (
	"context"
	"time"

	"github.com/google/uuid"
)

const clearTestFarms = `-- name: ClearTestFarms :exec
DELETE FROM farms
`

func (q *Queries) ClearTestFarms(ctx context.Context) error {
	_, err := q.db.ExecContext(ctx, clearTestFarms)
	return err
}

const createFarm = `-- name: CreateFarm :one
INSERT INTO farms (
  name, about, date_started, thumbnail, user_id
) VALUES (
  $1, $2, $3, $4, $5
) RETURNING id, name, thumbnail, about, date_started, user_id, created_at, updated_at, deleted_at
`

type CreateFarmParams struct {
	Name        string    `json:"name"`
	About       string    `json:"about"`
	DateStarted time.Time `json:"date_started"`
	Thumbnail   string    `json:"thumbnail"`
	UserID      uuid.UUID `json:"user_id"`
}

func (q *Queries) CreateFarm(ctx context.Context, arg CreateFarmParams) (Farm, error) {
	row := q.db.QueryRowContext(ctx, createFarm,
		arg.Name,
		arg.About,
		arg.DateStarted,
		arg.Thumbnail,
		arg.UserID,
	)
	var i Farm
	err := row.Scan(
		&i.ID,
		&i.Name,
		&i.Thumbnail,
		&i.About,
		&i.DateStarted,
		&i.UserID,
		&i.CreatedAt,
		&i.UpdatedAt,
		&i.DeletedAt,
	)
	return i, err
}

const getFarmByID = `-- name: GetFarmByID :one
SELECT id, name, about, date_started, thumbnail, created_at, updated_at FROM farms
WHERE id = $1 AND deleted_at IS NULL
`

type GetFarmByIDRow struct {
	ID          uuid.UUID `json:"id"`
	Name        string    `json:"name"`
	About       string    `json:"about"`
	DateStarted time.Time `json:"date_started"`
	Thumbnail   string    `json:"thumbnail"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

func (q *Queries) GetFarmByID(ctx context.Context, id uuid.UUID) (GetFarmByIDRow, error) {
	row := q.db.QueryRowContext(ctx, getFarmByID, id)
	var i GetFarmByIDRow
	err := row.Scan(
		&i.ID,
		&i.Name,
		&i.About,
		&i.DateStarted,
		&i.Thumbnail,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getFarmsBelongingToUser = `-- name: GetFarmsBelongingToUser :many
SELECT id, name, about, date_started, thumbnail, created_at, updated_at FROM farms
WHERE user_id = $1 AND deleted_at IS NULL
`

type GetFarmsBelongingToUserRow struct {
	ID          uuid.UUID `json:"id"`
	Name        string    `json:"name"`
	About       string    `json:"about"`
	DateStarted time.Time `json:"date_started"`
	Thumbnail   string    `json:"thumbnail"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

func (q *Queries) GetFarmsBelongingToUser(ctx context.Context, userID uuid.UUID) ([]GetFarmsBelongingToUserRow, error) {
	rows, err := q.db.QueryContext(ctx, getFarmsBelongingToUser, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []GetFarmsBelongingToUserRow{}
	for rows.Next() {
		var i GetFarmsBelongingToUserRow
		if err := rows.Scan(
			&i.ID,
			&i.Name,
			&i.About,
			&i.DateStarted,
			&i.Thumbnail,
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

const updateFarmDetails = `-- name: UpdateFarmDetails :one
UPDATE farms SET about = $1, thumbnail = $2
WHERE id = $3
RETURNING id, name, thumbnail, about, date_started, user_id, created_at, updated_at, deleted_at
`

type UpdateFarmDetailsParams struct {
	About     string    `json:"about"`
	Thumbnail string    `json:"thumbnail"`
	ID        uuid.UUID `json:"id"`
}

func (q *Queries) UpdateFarmDetails(ctx context.Context, arg UpdateFarmDetailsParams) (Farm, error) {
	row := q.db.QueryRowContext(ctx, updateFarmDetails, arg.About, arg.Thumbnail, arg.ID)
	var i Farm
	err := row.Scan(
		&i.ID,
		&i.Name,
		&i.Thumbnail,
		&i.About,
		&i.DateStarted,
		&i.UserID,
		&i.CreatedAt,
		&i.UpdatedAt,
		&i.DeletedAt,
	)
	return i, err
}
