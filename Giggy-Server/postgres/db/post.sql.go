// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0
// source: post.sql

package db

import (
	"context"

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
