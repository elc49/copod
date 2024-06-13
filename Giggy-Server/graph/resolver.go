package graph

import "github.com/elc49/giggy-monorepo/Giggy-Server/controllers"

// This file will not be regenerated automatically.
//
// It serves as dependency injection for your app, add any dependencies you require here.

type Resolver struct {
	postController controllers.PostController
}

func New(postController controllers.PostController) Config {
	resolver := &Resolver{
		postController,
	}

	c := Config{Resolvers: resolver}

	return c
}
