package tests

import (
	"context"
	"testing"
)

func TestPaymentController(t *testing.T) {
	ctx := context.Background()

	defer func() {
		queries.ClearTestUsers(ctx)
		queries.ClearTestFarms(ctx)
		queries.ClearTestMarkets(ctx)
		queries.ClearTestOrders(ctx)
	}()

	t.Run("create_payment", func(t *testing.T) {
	})
}
