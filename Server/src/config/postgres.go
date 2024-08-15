package config

type Postgres struct {
	DbName     string
	DbUser     string
	Driver     string
	WriterHost string
	ReaderHost string
	DbPass     string
	Migrate    bool
	Migration  string
}
