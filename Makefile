# Tidy go packages
tidy:
	cd Server && go mod tidy
# Generate graphql resolvers and models
graphql:
	cd Server && go run github.com/99designs/gqlgen generate --verbose
# Sqlc
postgres:
	cd Server && sqlc generate
# Test
tests:
	cd Server && go test ./tests
# Run server
api:
	cd Server && go run .
