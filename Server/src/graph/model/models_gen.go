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
	ID              uuid.UUID  `json:"id"`
	Name            string     `json:"name"`
	Thumbnail       string     `json:"thumbnail"`
	About           *string    `json:"about,omitempty"`
	DateStarted     time.Time  `json:"dateStarted"`
	UserID          uuid.UUID  `json:"userId"`
	Rating          float64    `json:"rating"`
	Reviewers       int        `json:"reviewers"`
	CompletedOrders int        `json:"completed_orders"`
	AddressString   string     `json:"address_string"`
	CreatedAt       time.Time  `json:"created_at"`
	UpdatedAt       time.Time  `json:"updated_at"`
	DeletedAt       *time.Time `json:"deleted_at,omitempty"`
}

type GetFarmMarketsInput struct {
	FarmID uuid.UUID  `json:"farmId"`
	Market MarketType `json:"market"`
}

type GetLocalizedMachineryMarketsInput struct {
	Radius *GpsInput `json:"radius"`
}

type GetLocalizedMarketsInput struct {
	Radius *GpsInput  `json:"radius"`
	Market MarketType `json:"market"`
}

type GpsInput struct {
	Lat float64 `json:"lat"`
	Lng float64 `json:"lng"`
}

type Market struct {
	ID            uuid.UUID    `json:"id"`
	Name          string       `json:"name"`
	Image         string       `json:"image"`
	Volume        int          `json:"volume"`
	Details       string       `json:"details"`
	RunningVolume int          `json:"running_volume"`
	Unit          MetricUnit   `json:"unit"`
	Type          MarketType   `json:"type"`
	Status        MarketStatus `json:"status"`
	Farm          *Farm        `json:"farm"`
	FarmID        uuid.UUID    `json:"farmId"`
	CanOrder      bool         `json:"canOrder"`
	PricePerUnit  int          `json:"pricePerUnit"`
	CreatedAt     time.Time    `json:"created_at"`
	UpdatedAt     time.Time    `json:"updated_at"`
}

type Mutation struct {
}

type NewFarmInput struct {
	Name        string    `json:"name"`
	About       string    `json:"about"`
	Location    *GpsInput `json:"location"`
	DateStarted string    `json:"dateStarted"`
	Thumbnail   string    `json:"thumbnail"`
}

type NewFarmMarketInput struct {
	FarmID       uuid.UUID  `json:"farmId"`
	Product      string     `json:"product"`
	Details      string     `json:"details"`
	Image        string     `json:"image"`
	Volume       int        `json:"volume"`
	Type         MarketType `json:"type"`
	Location     *GpsInput  `json:"location"`
	Unit         MetricUnit `json:"unit"`
	PricePerUnit int        `json:"pricePerUnit"`
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
	TrackingID uuid.UUID   `json:"trackingId"`
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

type PaystackPaymentUpdate struct {
	ReferenceID string    `json:"referenceId"`
	Status      string    `json:"status"`
	SessionID   uuid.UUID `json:"sessionId"`
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

type SetMarketStatusInput struct {
	ID     uuid.UUID    `json:"id"`
	Status MarketStatus `json:"status"`
}

type Subscription struct {
}

type UpdateFarmDetailsInput struct {
	ID        uuid.UUID `json:"id"`
	About     string    `json:"about"`
	Thumbnail string    `json:"thumbnail"`
}

type UpdateOrderStatusInput struct {
	ID     uuid.UUID   `json:"id"`
	Status OrderStatus `json:"status"`
}

type MarketStatus string

const (
	MarketStatusOpen   MarketStatus = "OPEN"
	MarketStatusClosed MarketStatus = "CLOSED"
	MarketStatusBooked MarketStatus = "BOOKED"
)

var AllMarketStatus = []MarketStatus{
	MarketStatusOpen,
	MarketStatusClosed,
	MarketStatusBooked,
}

func (e MarketStatus) IsValid() bool {
	switch e {
	case MarketStatusOpen, MarketStatusClosed, MarketStatusBooked:
		return true
	}
	return false
}

func (e MarketStatus) String() string {
	return string(e)
}

func (e *MarketStatus) UnmarshalGQL(v interface{}) error {
	str, ok := v.(string)
	if !ok {
		return fmt.Errorf("enums must be strings")
	}

	*e = MarketStatus(str)
	if !e.IsValid() {
		return fmt.Errorf("%s is not a valid MarketStatus", str)
	}
	return nil
}

func (e MarketStatus) MarshalGQL(w io.Writer) {
	fmt.Fprint(w, strconv.Quote(e.String()))
}

type MarketType string

const (
	MarketTypeSeeds     MarketType = "SEEDS"
	MarketTypeSeedlings MarketType = "SEEDLINGS"
	MarketTypeMachinery MarketType = "MACHINERY"
	MarketTypeHarvest   MarketType = "HARVEST"
)

var AllMarketType = []MarketType{
	MarketTypeSeeds,
	MarketTypeSeedlings,
	MarketTypeMachinery,
	MarketTypeHarvest,
}

func (e MarketType) IsValid() bool {
	switch e {
	case MarketTypeSeeds, MarketTypeSeedlings, MarketTypeMachinery, MarketTypeHarvest:
		return true
	}
	return false
}

func (e MarketType) String() string {
	return string(e)
}

func (e *MarketType) UnmarshalGQL(v interface{}) error {
	str, ok := v.(string)
	if !ok {
		return fmt.Errorf("enums must be strings")
	}

	*e = MarketType(str)
	if !e.IsValid() {
		return fmt.Errorf("%s is not a valid MarketType", str)
	}
	return nil
}

func (e MarketType) MarshalGQL(w io.Writer) {
	fmt.Fprint(w, strconv.Quote(e.String()))
}

type MetricUnit string

const (
	MetricUnitKg    MetricUnit = "Kg"
	MetricUnitGram  MetricUnit = "Gram"
	MetricUnitLitre MetricUnit = "Litre"
	MetricUnitHour  MetricUnit = "Hour"
	MetricUnitPiece MetricUnit = "Piece"
)

var AllMetricUnit = []MetricUnit{
	MetricUnitKg,
	MetricUnitGram,
	MetricUnitLitre,
	MetricUnitHour,
	MetricUnitPiece,
}

func (e MetricUnit) IsValid() bool {
	switch e {
	case MetricUnitKg, MetricUnitGram, MetricUnitLitre, MetricUnitHour, MetricUnitPiece:
		return true
	}
	return false
}

func (e MetricUnit) String() string {
	return string(e)
}

func (e *MetricUnit) UnmarshalGQL(v interface{}) error {
	str, ok := v.(string)
	if !ok {
		return fmt.Errorf("enums must be strings")
	}

	*e = MetricUnit(str)
	if !e.IsValid() {
		return fmt.Errorf("%s is not a valid MetricUnit", str)
	}
	return nil
}

func (e MetricUnit) MarshalGQL(w io.Writer) {
	fmt.Fprint(w, strconv.Quote(e.String()))
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
