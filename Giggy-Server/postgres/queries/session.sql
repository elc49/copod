-- name: CreateSessionByPhone :one
INSERT INTO sessions (
  ip, user_id, expires
) VALUES (
  $1, $2, $3
) RETURNING *;
