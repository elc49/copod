FROM golang:1.22

RUN mkdir -p go/src/copod
WORKDIR go/src/copod
COPY . .
RUN make tidy
#RUN CGO_ENABLED=0 GOOS=linux go build -o copod

EXPOSE 4000
EXPOSE 4001

RUN make copod
