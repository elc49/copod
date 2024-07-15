// Code generated by github.com/99designs/gqlgen, DO NOT EDIT.

package model

import (
	"fmt"
	"io"
	"strconv"
	"time"

	"github.com/google/uuid"
)

type AddToCartInput struct {
	Volume   int       `json:"volume"`
	MarketID uuid.UUID `json:"marketId"`
	FarmID   uuid.UUID `json:"farmId"`
}

type Address struct {
	AddressString string `json:"addressString"`
	Coords        *Gps   `json:"coords"`
}

type Cart struct {
	ID        uuid.UUID `json:"id"`
	Volume    int       `json:"volume"`
	FarmID    uuid.UUID `json:"farm_id"`
	Farm      *Farm     `json:"farm"`
	MarketID  uuid.UUID `json:"market_id"`
	Market    *Market   `json:"market"`
	UserID    uuid.UUID `json:"user_id"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type Farm struct {
	ID        uuid.UUID  `json:"id"`
	Name      string     `json:"name"`
	Thumbnail string     `json:"thumbnail"`
	UserID    uuid.UUID  `json:"userId"`
	CreatedAt time.Time  `json:"created_at"`
	UpdatedAt time.Time  `json:"updated_at"`
	DeletedAt *time.Time `json:"deleted_at,omitempty"`
}

type GpsInput struct {
	Lat float64 `json:"lat"`
	Lng float64 `json:"lng"`
}

type Market struct {
	ID           uuid.UUID `json:"id"`
	Name         string    `json:"name"`
	Image        string    `json:"image"`
	Volume       int       `json:"volume"`
	Unit         string    `json:"unit"`
	FarmID       uuid.UUID `json:"farmId"`
	Tag          string    `json:"tag"`
	PricePerUnit int       `json:"pricePerUnit"`
	CreatedAt    time.Time `json:"created_at"`
	UpdatedAt    time.Time `json:"updated_at"`
}

type Mutation struct {
}

type NewFarmInput struct {
	Name      string `json:"name"`
	Thumbnail string `json:"thumbnail"`
}

type NewFarmMarketInput struct {
	FarmID       uuid.UUID `json:"farmId"`
	Product      string    `json:"product"`
	Image        string    `json:"image"`
	Volume       int       `json:"volume"`
	Location     *GpsInput `json:"location"`
	Tag          string    `json:"tag"`
	Unit         string    `json:"unit"`
	PricePerUnit int       `json:"pricePerUnit"`
}

type NewPostInput struct {
	Text     string    `json:"text"`
	Image    string    `json:"image"`
	Tags     []string  `json:"tags"`
	UserID   uuid.UUID `json:"userId"`
	Location *GpsInput `json:"location"`
}

type Order struct {
	ID         uuid.UUID   `json:"id"`
	Volume     int         `json:"volume"`
	ToBePaid   int         `json:"toBePaid"`
	Currency   string      `json:"currency"`
	CustomerID uuid.UUID   `json:"customerId"`
	MarketID   uuid.UUID   `json:"marketId"`
	Market     *Market     `json:"market"`
	Status     OrderStatus `json:"status"`
	Customer   *User       `json:"customer"`
	CreatedAt  time.Time   `json:"created_at"`
	UpdatedAt  time.Time   `json:"updated_at"`
}

type PayWithMpesa struct {
	ReferenceID string `json:"referenceId"`
}

type PayWithMpesaInput struct {
	Amount   int    `json:"amount"`
	Currency string `json:"currency"`
	Phone    string `json:"phone"`
	Reason   string `json:"reason"`
}

type Payment struct {
	ID        uuid.UUID `json:"id"`
	Customer  string    `json:"customer"`
	Amount    int       `json:"amount"`
	Reason    string    `json:"reason"`
	Status    string    `json:"status"`
	OrderID   uuid.UUID `json:"orderId"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

type PaystackPaymentVerificationStatus struct {
	Status    string    `json:"status"`
	SessionID uuid.UUID `json:"sessionId"`
}

type Post struct {
	ID          uuid.UUID `json:"id"`
	Text        string    `json:"text"`
	Image       string    `json:"image"`
	Tags        []string  `json:"tags"`
	FarmAddress *Address  `json:"farmAddress"`
	UserID      uuid.UUID `json:"userId"`
	User        *User     `json:"user"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

type Query struct {
}

type SendOrderToFarmInput struct {
	CartID   uuid.UUID `json:"cartId"`
	Volume   int       `json:"volume"`
	ToBePaid int       `json:"toBePaid"`
	Currency string    `json:"currency"`
	MarketID uuid.UUID `json:"marketId"`
	FarmID   uuid.UUID `json:"farmId"`
}

type OrderStatus string

const (
	OrderStatusPending   OrderStatus = "PENDING"
	OrderStatusConfirmed OrderStatus = "CONFIRMED"
	OrderStatusDelivered OrderStatus = "DELIVERED"
	OrderStatusCancelled OrderStatus = "CANCELLED"
)

var AllOrderStatus = []OrderStatus{
	OrderStatusPending,
	OrderStatusConfirmed,
	OrderStatusDelivered,
	OrderStatusCancelled,
}

func (e OrderStatus) IsValid() bool {
	switch e {
	case OrderStatusPending, OrderStatusConfirmed, OrderStatusDelivered, OrderStatusCancelled:
		return true
	}
	return false
}

func (e OrderStatus) String() string {
	return string(e)
}

func (e *OrderStatus) UnmarshalGQL(v interface{}) error {
	str, ok := v.(string)
	if !ok {
		return fmt.Errorf("enums must be strings")
	}

	*e = OrderStatus(str)
	if !e.IsValid() {
		return fmt.Errorf("%s is not a valid OrderStatus", str)
	}
	return nil
}

func (e OrderStatus) MarshalGQL(w io.Writer) {
	fmt.Fprint(w, strconv.Quote(e.String()))
}
