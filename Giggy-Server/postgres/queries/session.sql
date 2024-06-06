-- name: CreateSessionByPhone :one
INSERT INTO sessions (
  ip, user_id
) VALUES (
  $1, $2
) RETURNING *;

-- name: ClearTestSessions :exec
DELETE FROM sessions;
