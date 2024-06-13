-- name: CreatePost :one
INSERT INTO posts (
  text, image, tags, user_id, location
) VALUES (
  $1, $2, $3, $4, sqlc.arg(location)
) RETURNING *;
