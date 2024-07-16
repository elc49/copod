// Code generated by sqlc. DO NOT EDIT.
// versions:
//   sqlc v1.24.0
// source: order.sql

package db

import (
	"context"

	"github.com/google/uuid"
)

const clearTestOrders = `-- name: ClearTestOrders :exec
DELETE FROM orders
`

func (q *Queries) ClearTestOrders(ctx context.Context) error {
	_, err := q.db.ExecContext(ctx, clearTestOrders)
	return err
}

const createOrder = `-- name: CreateOrder :one
INSERT INTO orders (
  volume, to_be_paid, currency, customer_id, market_id, farm_id
) VALUES (
  $1, $2, $3, $4, $5, $6
)
RETURNING id, volume, status, to_be_paid, currency, customer_id, market_id, farm_id, created_at, updated_at
`

type CreateOrderParams struct {
	Volume     int32     `json:"volume"`
	ToBePaid   int32     `json:"to_be_paid"`
	Currency   string    `json:"currency"`
	CustomerID uuid.UUID `json:"customer_id"`
	MarketID   uuid.UUID `json:"market_id"`
	FarmID     uuid.UUID `json:"farm_id"`
}

func (q *Queries) CreateOrder(ctx context.Context, arg CreateOrderParams) (Order, error) {
	row := q.db.QueryRowContext(ctx, createOrder,
		arg.Volume,
		arg.ToBePaid,
		arg.Currency,
		arg.CustomerID,
		arg.MarketID,
		arg.FarmID,
	)
	var i Order
	err := row.Scan(
		&i.ID,
		&i.Volume,
		&i.Status,
		&i.ToBePaid,
		&i.Currency,
		&i.CustomerID,
		&i.MarketID,
		&i.FarmID,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getOrderById = `-- name: GetOrderById :one
SELECT id, volume, status, to_be_paid, currency, customer_id, market_id, farm_id, created_at, updated_at FROM orders
WHERE id = $1
`

func (q *Queries) GetOrderById(ctx context.Context, id uuid.UUID) (Order, error) {
	row := q.db.QueryRowContext(ctx, getOrderById, id)
	var i Order
	err := row.Scan(
		&i.ID,
		&i.Volume,
		&i.Status,
		&i.ToBePaid,
		&i.Currency,
		&i.CustomerID,
		&i.MarketID,
		&i.FarmID,
		&i.CreatedAt,
		&i.UpdatedAt,
	)
	return i, err
}

const getOrdersBelongingToFarm = `-- name: GetOrdersBelongingToFarm :many
SELECT id, volume, status, to_be_paid, currency, customer_id, market_id, farm_id, created_at, updated_at FROM orders
WHERE farm_id = $1
`

func (q *Queries) GetOrdersBelongingToFarm(ctx context.Context, farmID uuid.UUID) ([]Order, error) {
	rows, err := q.db.QueryContext(ctx, getOrdersBelongingToFarm, farmID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Order{}
	for rows.Next() {
		var i Order
		if err := rows.Scan(
			&i.ID,
			&i.Volume,
			&i.Status,
			&i.ToBePaid,
			&i.Currency,
			&i.CustomerID,
			&i.MarketID,
			&i.FarmID,
			&i.CreatedAt,
			&i.UpdatedAt,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getOrdersBelongingToUser = `-- name: GetOrdersBelongingToUser :many
SELECT id, volume, status, to_be_paid, currency, customer_id, market_id, farm_id, created_at, updated_at FROM orders
WHERE customer_id = $1
`

func (q *Queries) GetOrdersBelongingToUser(ctx context.Context, customerID uuid.UUID) ([]Order, error) {
	rows, err := q.db.QueryContext(ctx, getOrdersBelongingToUser, customerID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	items := []Order{}
	for rows.Next() {
		var i Order
		if err := rows.Scan(
			&i.ID,
			&i.Volume,
			&i.Status,
			&i.ToBePaid,
			&i.Currency,
			&i.CustomerID,
			&i.MarketID,
			&i.FarmID,
			&i.CreatedAt,
			&i.UpdatedAt,
		); err != nil {
			return nil, err
		}
		items = append(items, i)
	}
	if err := rows.Close(); err != nil {
		return nil, err
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

const getUserOrdersCount = `-- name: GetUserOrdersCount :one
SELECT count(*) FROM orders
WHERE customer_id = $1 AND status = 'PENDING'
`

func (q *Queries) GetUserOrdersCount(ctx context.Context, customerID uuid.UUID) (int64, error) {
	row := q.db.QueryRowContext(ctx, getUserOrdersCount, customerID)
	var count int64
	err := row.Scan(&count)
	return count, err
}
