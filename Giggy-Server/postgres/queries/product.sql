-- name: GetProductsBelongingToStore :many
SELECT id, name, image, volume, unit, price_per_unit, created_at, updated_at FROM products
WHERE store_id = $1;

-- name: GetProductByID :one
SELECT id, name, image, volume, unit, price_per_unit, created_at, updated_at FROM products
WHERE id = $1;
