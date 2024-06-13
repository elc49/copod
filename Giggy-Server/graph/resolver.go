package graph

import (
	"github.com/elc49/giggy-monorepo/Giggy-Server/controllers"
	"github.com/elc49/giggy-monorepo/Giggy-Server/logger"
	"github.com/google/uuid"
)

// This file will not be regenerated automatically.
//
// It serves as dependency injection for your app, add any dependencies you require here.

var log = logger.GetLogger()

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

func stringToUUID(id string) uuid.UUID {
	uid, err := uuid.Parse(id)
	if err != nil {
		log.WithError(err).Error("resolver: stringToUUID()")
	}

	return uid
}
