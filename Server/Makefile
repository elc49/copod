# Tidy go packages
tidy:
	go mod tidy
# Generate graphql resolvers and models
graphql:
	go run github.com/99designs/gqlgen generate --verbose
# Sqlc
postgres:
	sqlc generate
# Test
test:
	go test ./tests
# Run server
copod:
	go run .
