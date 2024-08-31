-- name: AddToCart :one
INSERT INTO carts (
  volume, market_id, farm_id, user_id
) VALUES (
  $1, $2, $3, $4
)
RETURNING *;

-- name: GetCartItem :one
SELECT * FROM carts
WHERE market_id = $1 AND farm_id = $2 AND user_id = $3
LIMIT 1;

-- name: UpdateCartVolume :one
UPDATE carts SET volume = $1
WHERE id = $2
RETURNING *;

-- name: GetUserCartItems :many
SELECT * FROM carts
WHERE user_id = $1;

-- name: DeleteCartItem :exec
DELETE FROM carts WHERE market_id = $1;

-- name: ClearTestCarts :exec
DELETE FROM carts;
