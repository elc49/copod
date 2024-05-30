package model

type Ipinfo struct {
	Ip                 string `json:"ip"`
	Version            string `json:"version"`
	CountryCallingCode string `json:"country_calling_code"`
	CountryCodeISO3    string `json:"country_code_iso3"`
	CountryName        string `json:"country_name"`
	CountryFlagURL     string `json:"country_flag_url"`
	Currency           string `json:"currency"`
	CurrencyName       string `json:"currency_name"`
}
