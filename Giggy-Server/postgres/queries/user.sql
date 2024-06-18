-- name: CountUsers :one
SELECT COUNT(*) FROM users;

-- name: CreateUserByPhone :one
INSERT INTO users (
  phone, username, avatar
) VALUES (
  $1, $2, $3
) RETURNING *;

-- name: GetUserByPhone :one
SELECT id, phone FROM users
WHERE phone = $1 AND deleted_at IS NULL;

-- name: GetUserByID :one
SELECT id, phone FROM users
WHERE id = $1 AND deleted_at IS NULL;

-- name: ClearTestUsers :exec
DELETE FROM users;
