-- name: CreateFarm :one
INSERT INTO farms (
  name, thumbnail, user_id
) VALUES (
  $1, $2, $3
) RETURNING *;

-- name: GetFarmsBelongingToUser :many
SELECT id, name, thumbnail, created_at, updated_at FROM farms
WHERE user_id = $1 AND deleted_at IS NULL;

-- name: GetFarmByID :one
SELECT id, name, thumbnail, created_at, updated_at FROM farms
WHERE id = $1 AND deleted_at IS NULL;

-- name: ClearTestFarms :exec
DELETE FROM farms;
