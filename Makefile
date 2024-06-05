# Tidy go packages
tidy:
	cd Giggy-Server && go mod tidy
# Generate graphql resolvers and models
graphql:
	cd Giggy-Server && go run github.com/99designs/gqlgen generate --verbose
# Sqlc
postgres:
	cd Giggy-Server && sqlc generate
# Test
tests:
	cd Giggy-Server && go test -v ./tests
# Run server
server:
	cd Giggy-Server && go run .
