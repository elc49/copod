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

func ServiceFeesByCountry(category, countryISOCode string) int {
	countryFees := map[string]map[string]int{
		"farming_rights": {"US": 25, "KES": 2000},
		"poster_rights":  {"US": 30, "KES": 2500},
	}

	return countryFees[category][countryISOCode]
}
