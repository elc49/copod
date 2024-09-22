-- name: CreateFarm :one
INSERT INTO farms (
  name, about, date_started, address_string, thumbnail, user_id, location
) VALUES (
  $1, $2, $3, $4, $5, $6, sqlc.arg(location)
) RETURNING *;

-- name: GetFarmsBelongingToUser :many
SELECT id, name, about, date_started, thumbnail, created_at, updated_at FROM farms
WHERE user_id = $1 AND deleted_at IS NULL;

-- name: GetFarmByID :one
SELECT id, name, about, address_string, date_started, thumbnail, created_at, updated_at FROM farms
WHERE id = $1 AND deleted_at IS NULL;

-- name: UpdateFarmDetails :one
UPDATE farms SET about = $1, thumbnail = $2
WHERE id = $3
RETURNING *;

-- name: FarmRatingPoints :one
SELECT rate FROM ratings
WHERE farm_id = $1;

-- name: CountFarmReviewers :one
SELECT COUNT(*) FROM ratings
WHERE farm_id = $1;

-- name: ClearTestFarms :exec
DELETE FROM farms;
