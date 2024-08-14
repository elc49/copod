package tests

import (
	"context"
	"testing"
)

func TestPaymentController(t *testing.T) {
	ctx := context.Background()

	defer func() {
		store.StoreWriter.ClearTestUsers(ctx)
		store.StoreWriter.ClearTestFarms(ctx)
		store.StoreWriter.ClearTestMarkets(ctx)
		store.StoreWriter.ClearTestOrders(ctx)
	}()
}
