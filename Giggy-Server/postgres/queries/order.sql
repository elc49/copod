-- name: GetOrdersBelongingToFarm :many
SELECT id, volume, to_be_paid, customer_id, market_id, farm_id, created_at, updated_at FROM orders
WHERE farm_id = $1;

-- name: GetOrderById :one
SELECT id, volume, to_be_paid, customer_id, market_id, created_at, updated_at FROM orders
WHERE id = $1;
