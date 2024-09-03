package postgres

import (
	"database/sql"
	"fmt"

	"github.com/elc49/copod/Server/src/config"
	"github.com/elc49/copod/Server/src/logger"
	"github.com/elc49/copod/Server/src/postgres/db"
	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/postgres"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "github.com/lib/pq"
)

type Store struct {
	StoreReader, StoreWriter *db.Queries
}

func InitWriter(option config.Rdbms) *db.Queries {
	log := logger.GetLogger()
	uri := fmt.Sprintf("user=%s password=%s host=%s dbname=%s", option.Postgres.DbUser, option.Postgres.DbPass, option.Postgres.WriterHost, option.Postgres.DbName)
	if !isProd() {
		uri += " sslmode=disable"
	}

	dbConn, err := sql.Open(option.Postgres.Driver, uri)
	if err != nil {
		log.WithError(err).Fatalln("postgres: db connect")
		return nil
	}

	if err := dbConn.Ping(); err != nil {
		log.WithError(err).Fatalln("postgres: ping InitWriter")
		return nil
	} else {
		log.Infoln("Store writer connection...OK")
	}

	dbConn.Exec(fmt.Sprintf("CREATE EXTENSION IF NOT EXISTS %q;", "uuid-ossp"))
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis;")
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis_rasters; --OPTIONAL")
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis_topology; --OPTIONAL")

	// Setup postgres tables schema
	if err := runMigration(option.Postgres.Migration, option.Postgres.Migrate, dbConn); err != nil {
		log.WithError(err).Fatalln("postgres: runMigration")
	} else {
		log.Infoln("Store writer tables schema...OK")
	}

	dB := db.New(dbConn)

	return dB
}

func InitReader(option config.Rdbms) *db.Queries {
	log := logger.GetLogger()
	uri := fmt.Sprintf("user=%s password=%s host=%s dbname=%s", option.Postgres.DbUser, option.Postgres.DbPass, option.Postgres.ReaderHost, option.Postgres.DbName)
	if !isProd() {
		uri += " sslmode=disable"
	}

	dbConn, err := sql.Open(option.Postgres.Driver, uri)
	if err != nil {
		log.WithError(err).Fatalln("postgres: db connect")
		return nil
	}

	if err := dbConn.Ping(); err != nil {
		log.WithError(err).Fatalln("postgres: ping InitReader")
		return nil
	} else {
		log.Infoln("Store reader connection...OK")
	}

	dbConn.Exec(fmt.Sprintf("CREATE EXTENSION IF NOT EXISTS %q;", "uuid-ossp"))
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis;")
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis_rasters; --OPTIONAL")
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis_topology; --OPTIONAL")

	dB := db.New(dbConn)

	return dB
}

func runMigration(migration string, migrateTables bool, conn *sql.DB) error {
	driver, err := postgres.WithInstance(conn, &postgres.Config{})
	if err != nil {
		return err
	}

	m, err := migrate.NewWithDatabaseInstance(migration, "postgres", driver)
	if err != nil {
		return err
	}

	if migrateTables {
		if err := m.Down(); err != nil && err != migrate.ErrNoChange {
			return err
		}
	}

	if err := m.Up(); err != nil && err != migrate.ErrNoChange {
		return err
	}

	return nil
}

func isProd() bool {
	if config.Configuration == nil {
		return false
	}

	return config.Configuration.Server.Env == "prod" ||
		config.Configuration.Server.Env == "staging"
}
