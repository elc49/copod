-- name: GetMarketsBelongingToFarm :many
SELECT id, product, image, volume, type, running_volume, unit, farm_id, status, price_per_unit, harvest_date, created_at, updated_at FROM markets
WHERE farm_id = $1 AND type = $2;

-- name: GetMarketByID :one
SELECT id, product, type, details, image, volume, running_volume, status, unit, farm_id, price_per_unit, created_at, updated_at FROM markets
WHERE id = $1;

-- name: CreateFarmMarket :one
INSERT INTO markets (
  product, details, type, image, volume, running_volume, unit, harvest_date, price_per_unit, farm_id, location
) VALUES (
  $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, sqlc.arg(location)
)
RETURNING *;

-- name: GetLocalizedMarkets :many
SELECT id, product, image, details, price_per_unit, status, running_volume, volume, unit, farm_id, location, created_at, updated_at FROM markets
WHERE ST_DWithin(location, sqlc.arg(point)::geography, sqlc.arg(radius)) AND running_volume > 0 AND type = sqlc.arg(type);

-- name: GetLocalizedMachineryMarkets :many
SELECT id, product, image, details, price_per_unit, status, unit, farm_id, location, created_at, updated_at FROM markets
WHERE ST_DWithin(location, sqlc.arg(point)::geography, sqlc.arg(radius)) AND type = 'MACHINERY';

-- name: UpdateMarketVolume :one
UPDATE markets SET running_volume = $1
WHERE id = $2
RETURNING *;

-- name: GetFarmOwnerID :one
SELECT user_id FROM farms
WHERE id = $1;

-- name: SetMarketStatus :one
UPDATE markets SET status = $1
WHERE id = $2
RETURNING *;

-- name: ClearTestMarkets :exec
DELETE FROM markets;
