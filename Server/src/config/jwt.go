package config

import "time"

type Jwt struct {
	Expires time.Duration
	Secret  string
}
