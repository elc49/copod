-- name: GetMarketsBelongingToFarm :many
SELECT id, product, image, volume, unit, farm_id, price_per_unit, tag, harvest_date, created_at, updated_at FROM markets
WHERE farm_id = $1;

-- name: GetMarketByID :one
SELECT id, product, image, volume, running_volume, unit, farm_id, tag, price_per_unit, created_at, updated_at FROM markets
WHERE id = $1;

-- name: CreateFarmMarket :one
INSERT INTO markets (
  product, image, volume, running_volume, unit, harvest_date, tag, price_per_unit, farm_id, location
) VALUES (
  $1, $2, $3, $4, $5, $6, $7, $8, $9, sqlc.arg(location)
)
RETURNING *;

-- name: GetLocalizedMarkets :many
SELECT id, product, image, price_per_unit, running_volume, volume, unit, farm_id, location, created_at, updated_at FROM markets
WHERE ST_DWithin(location, sqlc.arg(point)::geography, sqlc.arg(radius)) AND running_volume > 0;

-- name: UpdateMarketVolume :one
UPDATE markets SET running_volume = $1
WHERE id = $2
RETURNING *;

-- name: GetFarmOwnerID :one
SELECT user_id FROM farms
WHERE id = $1;

-- name: ClearTestMarkets :exec
DELETE FROM markets;
