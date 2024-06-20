-- name: CreateStore :one
INSERT INTO stores (
  name, thumbnail, user_id
) VALUES (
  $1, $2, $3
) RETURNING *;

-- name: GetStoresBelongingToUser :many
SELECT id, name, thumbnail, created_at, updated_at FROM stores
WHERE user_id = $1 AND deleted_at IS NULL;

-- name: GetStoreByID :one
SELECT id, name, thumbnail, created_at, updated_at FROM stores
WHERE id = $1 AND deleted_at IS NULL;
