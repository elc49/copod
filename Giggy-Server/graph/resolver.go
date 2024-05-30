package graph

// This file will not be regenerated automatically.
//
// It serves as dependency injection for your app, add any dependencies you require here.

type Resolver struct{}

func New() Config {
	c := Config{Resolvers: &Resolver{}}
	return c
}
