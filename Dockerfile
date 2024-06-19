FROM golang:1.22

RUN mkdir -p go/src/app
WORKDIR go/src/app
COPY ./Giggy-Server/ .
RUN go mod download && go mod verify
RUN CGO_ENABLED=0 GOOS=linux go build -o giggy

EXPOSE 8080

CMD ["./giggy"]
