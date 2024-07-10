package model

import "github.com/google/uuid"

type ChargeMpesaPhoneInput struct {
	Amount   int       `json:"amount"`
	Email    string    `json:"email"`
	UserID   uuid.UUID `json:"user_id,omitempty"`
	Reason   string    `json:"reason,omitempty"`
	Currency string    `json:"currency"`
	Provider struct {
		Phone    string `json:"phone"`
		Provider string `json:"provider"`
	} `json:"mobile_money"`
}

type ChargeMpesaPhoneRes struct {
	Status  bool   `json:"status"`
	Message string `json:"message"`
	Data    struct {
		Reference   string `json:"reference"`
		Status      string `json:"status"`
		DisplayText string `json:"display_text"`
	} `json:"data"`
}

type ChargeMpesaPhoneCallbackRes struct {
	Event string `json:"event"`
	Data  struct {
		Status    string `json:"status"`
		Reference string `json:"reference"`
		Amount    int    `json:"amount"`
		PaidAt    string `json:"paid_at"`
		Customer  struct {
			ID           int    `json:"id"`
			Phone        string `json:"phone"`
			CustomerCode string `json:"customer_code"`
			Email        string `json:"email"`
		} `json:"customer"`
		CreatedAt     string `json:"created_at"`
		Channel       string `json:"channel"`
		Currency      string `json:"currency"`
		Fees          int    `json:"fees"`
		Authorization struct {
			Bank        string `json:"bank"`
			Channel     string `json:"channel"`
			CountryCode string `json:"country_code"`
			Brand       string `json:"brand"`
			AuthCode    string `json:"authorization_code"`
		} `json:"authorization"`
	} `json:"data"`
}

type MpesaTransactionVerification struct {
	Status  bool   `json:"status"`
	Message string `json:"message"`
	Data    struct {
		ID        int    `json:"id"`
		Domain    string `json:"domain"`
		Status    string `json:"status"`
		Reference string `json:"reference"`
		Amount    int    `json:"amount"`
		PaidAt    string `json:"paid_at"`
	} `json:"data"`
}
