package config

type Postgres struct {
	DbName     string
	DbUser     string
	Driver     string
	Port       string
	WriterHost string
	ReaderHost string
	DbPass     string
	Migrate    bool
	Migration  string
}
