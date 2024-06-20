-- name: GetProductsBelongingToStore :many
SELECT id, name, image, volume, unit, price_per_unit, created_at, updated_at FROM products
WHERE store_id = $1;

-- name: GetProductByID :one
SELECT id, name, image, volume, unit, price_per_unit, created_at, updated_at FROM products
WHERE id = $1;

-- name: CreateStoreProduct :one
INSERT INTO products (
  name, image, volume, unit, price_per_unit, store_id
) VALUES (
  $1, $2, $3, $4, $5, $6
)
RETURNING *;
