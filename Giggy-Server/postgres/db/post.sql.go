// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0
// source: post.sql

package db

import (
	"context"
	"time"

	"github.com/google/uuid"
	"github.com/lib/pq"
)

const clearTestPosters = `-- name: ClearTestPosters :exec
DELETE FROM posts
`

func (q *Queries) ClearTestPosters(ctx context.Context) error {
	_, err := q.db.ExecContext(ctx, clearTestPosters)
	return err
}

const createPost = `-- name: CreatePost :one
INSERT INTO posts (
  text, image, tags, user_id, location
) VALUES (
  $1, $2, $3, $4, $5
) RETURNING id, text, image, tags, location, user_id, created_at, updated_at
`

type CreatePostParams struct {
	Text     string      `json:"text"`
	Image    string      `json:"image"`
	Tags     []string    `json:"tags"`
	UserID   uuid.UUID   `json:"user_id"`
	Location interface{} `json:"location"`
}

func (q *Queries) CreatePost(ctx context.Context, arg CreatePostParams) (Post, error) {
	row := q.db.QueryRowContext(ctx, createPost,
		arg.Text,
		arg.Image,
		pq.Array(arg.Tags),
		arg.UserID,
		arg.Location,
	)
	var i Post
	err := row.Scan(
		&i.ID,
		&i.Text,
		&i.Image,
		pq.Array(&i.Tags),
		&i.Location,
		&i.UserID,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getLocalizedPosters = `-- name: GetLocalizedPosters :many
SELECT id, text, image, tags, user_id, ST_AsGeoJSON(location) AS location, created_at, updated_at FROM posts
WHERE ST_DWithin(location, $1::geography, $2)
`

type GetLocalizedPostersParams struct {
	Point  interface{} `json:"point"`
	Radius interface{} `json:"radius"`
}

type GetLocalizedPostersRow struct {
	ID        uuid.UUID   `json:"id"`
	Text      string      `json:"text"`
	Image     string      `json:"image"`
	Tags      []string    `json:"tags"`
	UserID    uuid.UUID   `json:"user_id"`
	Location  interface{} `json:"location"`
	CreatedAt time.Time   `json:"created_at"`
	UpdatedAt time.Time   `json:"updated_at"`
}

func (q *Queries) GetLocalizedPosters(ctx context.Context, arg GetLocalizedPostersParams) ([]GetLocalizedPostersRow, error) {
	rows, err := q.db.QueryContext(ctx, getLocalizedPosters, arg.Point, arg.Radius)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []GetLocalizedPostersRow{}
	for rows.Next() {
		var i GetLocalizedPostersRow
		if err := rows.Scan(
			&i.ID,
			&i.Text,
			&i.Image,
			pq.Array(&i.Tags),
			&i.UserID,
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
