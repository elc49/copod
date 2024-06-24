-- name: GetPaymentsBelongingToFarm :many
SELECT id, customer, amount, status, created_at, updated_at FROM payments
WHERE farm_id = $1;
