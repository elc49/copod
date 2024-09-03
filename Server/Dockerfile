FROM golang:1.22

RUN mkdir -p go/src/copod
WORKDIR go/src/copod
COPY . .
RUN go mod download && go mod verify
RUN CGO_ENABLED=0 GOOS=linux go build -o copod

EXPOSE 8080

CMD ["./copod"]
