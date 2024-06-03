package config

type Rdbms struct {
	Driver        string
	Uri           string
	Migrate       bool
	MigrationFile string
}
