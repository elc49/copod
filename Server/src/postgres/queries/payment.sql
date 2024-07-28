-- name: GetPaymentsBelongingToFarm :many
SELECT id, customer, amount, currency, status, created_at, updated_at FROM payments
WHERE farm_id = $1;

-- name: BuyRights :one
INSERT INTO payments (
  customer, amount, currency, reason, status, reference_id, user_id
) VALUES (
  $1, $2, $3, $4, $5, $6, $7
)
RETURNING *;

-- name: UpdatePaystackPaymentStatus :one
UPDATE payments SET status = $1
WHERE reference_id = $2
RETURNING *;

-- name: GetRightPurchasePaymentByReferenceID :one
SELECT * FROM payments
WHERE reference_id = $1
LIMIT 1;
