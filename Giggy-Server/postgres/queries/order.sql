-- name: GetOrdersBelongingToStore :many
SELECT id, volume, to_be_paid, customer_id, product_id, created_at, updated_at FROM orders
WHERE store_id = $1;

-- name: GetOrderById :one
SELECT id, volume, to_be_paid, customer_id, product_id, created_at, updated_at FROM orders
WHERE id = $1;
