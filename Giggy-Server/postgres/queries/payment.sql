-- name: GetPaymentsBelongingToStore :many
SELECT id, customer, amount, status, created_at, updated_at FROM payments
WHERE store_id = $1;
