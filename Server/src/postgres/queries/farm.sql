-- name: CreateFarm :one
INSERT INTO farms (
  name, about, date_started, thumbnail, user_id
) VALUES (
  $1, $2, $3, $4, $5
) RETURNING *;

-- name: GetFarmsBelongingToUser :many
SELECT id, name, about, date_started, thumbnail, created_at, updated_at FROM farms
WHERE user_id = $1 AND deleted_at IS NULL;

-- name: GetFarmByID :one
SELECT id, name, about, date_started, thumbnail, created_at, updated_at FROM farms
WHERE id = $1 AND deleted_at IS NULL;

-- name: UpdateFarmDetails :one
UPDATE farms SET about = $1, thumbnail = $2
WHERE id = $3
RETURNING *;

-- name: ClearTestFarms :exec
DELETE FROM farms;
