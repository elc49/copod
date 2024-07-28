-- name: GetOrdersBelongingToFarm :many
SELECT * FROM orders
WHERE farm_id = $1;

-- name: GetOrderById :one
SELECT * FROM orders
WHERE id = $1;

-- name: CreateOrder :one
INSERT INTO orders (
  volume, to_be_paid, currency, customer_id, market_id, farm_id
) VALUES (
  $1, $2, $3, $4, $5, $6
)
RETURNING *;

-- name: GetOrdersBelongingToUser :many
SELECT * FROM orders
WHERE customer_id = $1;

-- name: GetUserOrdersCount :one
SELECT count(*) FROM orders
WHERE customer_id = $1 AND status = 'PENDING';

-- name: UpdateOrderStatus :one
UPDATE orders SET status = $1
WHERE id = $2
RETURNING *;

-- name: ClearTestOrders :exec
DELETE FROM orders;
