-- name: CreateOrderItem :one
INSERT INTO order_items (
  volume, market_id, order_id
) VALUES (
  $1, $2, $3
)
RETURNING *;

-- name: GetOrderItems :many
SELECT * FROM order_items
WHERE order_id = $1;

-- name: CountOrderItems :one
SELECT COUNT(*) FROM order_items
WHERE order_id = $1;
