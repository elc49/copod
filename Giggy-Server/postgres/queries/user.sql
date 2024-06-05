-- name: CountUsers :one
SELECT COUNT(*) FROM users;

-- name: CreateUserByPhone :one
INSERT INTO users (
  phone
) VALUES (
  $1
) RETURNING *;

-- name: GetUserByPhone :one
SELECT id, phone FROM users
WHERE phone = $1 AND deleted_at IS NULL;

-- name: GetUserByID :one
SELECT id, phone FROM users
WHERE id = $1 AND deleted_at IS NULL;
