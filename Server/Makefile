# Tidy go packages
tidy:
	go mod tidy
# Generate graphql resolvers and models
graphql-api:
	go run github.com/99designs/gqlgen generate --verbose
# Sqlc
postgres-db:
	sqlc generate
# Test
tests-run:
	go test ./tests
# Run server
vuno-api:
	go run .
