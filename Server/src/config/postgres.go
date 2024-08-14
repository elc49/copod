package config

type Postgres struct {
	DbName     string
	User       string
	Driver     string
	Port       string
	WriterHost string
	ReaderHost string
	Pass       string
	Migrate    bool
	Migration  string
}
