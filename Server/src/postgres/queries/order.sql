-- name: GetOrdersBelongingToFarm :many
SELECT * FROM orders
WHERE farm_id = $1 AND deleted_at IS NULL;

-- name: GetOrderByID :one
SELECT * FROM orders
WHERE id = $1 AND deleted_at IS NULL;

-- name: CreateOrder :one
INSERT INTO orders (
  to_be_paid, currency, customer_id, farm_id, customer_notification_tracking_id
) VALUES (
  $1, $2, $3, $4, $5
)
RETURNING *;

-- name: GetOrdersBelongingToUser :many
SELECT * FROM orders
WHERE customer_id = $1 AND deleted_at IS NULL;

-- name: GetUserOrdersCount :one
SELECT COUNT(*) FROM orders
WHERE customer_id = $1 AND status = 'PENDING' AND deleted_at IS NULL;

-- name: UpdateOrderStatus :one
UPDATE orders SET status = $1
WHERE id = $2 AND deleted_at IS NULL
RETURNING *;

-- name: CompletedFarmOrders :one
SELECT COUNT(*) FROM orders
WHERE farm_id = $1 AND status = $2;

-- name: DeleteOrder :exec
DELETE FROM orders WHERE id = $1;

-- name: ClearTestOrders :exec
DELETE FROM orders;
