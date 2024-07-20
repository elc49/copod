-- name: CreatePost :one
INSERT INTO posts (
  text, image, tags, user_id, address_string, location
) VALUES (
  $1, $2, $3, $4, $5, sqlc.arg(location)
) RETURNING *;

-- name: GetLocalizedPosters :many
SELECT id, text, image, tags, address_string, ST_AsGeoJSON(location) AS location, user_id, created_at, updated_at FROM posts
WHERE ST_DWithin(location, sqlc.arg(point)::geography, sqlc.arg(radius));

-- name: ClearTestPosters :exec
DELETE FROM posts;
