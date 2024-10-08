// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0
// source: review.sql

package db

import (
	"context"

	"github.com/google/uuid"
)

const farmRating = `-- name: FarmRating :one
SELECT SUM(rate) AS rating FROM ratings
WHERE farm_id = $1
`

func (q *Queries) FarmRating(ctx context.Context, farmID uuid.NullUUID) (int64, error) {
	row := q.db.QueryRowContext(ctx, farmRating, farmID)
	var rating int64
	err := row.Scan(&rating)
	return rating, err
}

const farmReviewers = `-- name: FarmReviewers :one
SELECT COUNT(*) AS reviewers FROM ratings
WHERE farm_id = $1
`

func (q *Queries) FarmReviewers(ctx context.Context, farmID uuid.NullUUID) (int64, error) {
	row := q.db.QueryRowContext(ctx, farmReviewers, farmID)
	var reviewers int64
	err := row.Scan(&reviewers)
	return reviewers, err
}
