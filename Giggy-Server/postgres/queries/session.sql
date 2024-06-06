-- name: CreateSessionByPhone :one
INSERT INTO sessions (
  ip, user_id, expires
) VALUES (
  $1, $2, $3
) RETURNING *;

-- name: GetSessionByUserID :one
SELECT id, ip, user_id, expires FROM sessions
WHERE NOW() < expires AND user_id = $1
LIMIT 1;

-- name: ClearTestSessions :exec
DELETE FROM sessions;
