package model

type Ipinfo struct {
	Ip                 string `json:"ip"`
	Version            string `json:"version"`
	CountryCallingCode string `json:"country_calling_code"`
	Gps                string `json:"gps"`
	PosterRightsFee    int    `json:"poster_rights_fee"`
	FarmingRightsFee   int    `json:"farming_rights_fee"`
	CountryCode        string `json:"country_code"`
	CountryName        string `json:"country_name"`
	CountryFlagURL     string `json:"country_flag_url"`
	Currency           string `json:"currency"`
	CurrencyName       string `json:"currency_name"`
	Languages          string `json:"languages"`
}
