package postgres

import (
	"database/sql"
	"fmt"

	"github.com/elc49/vuno/Server/src/config"
	"github.com/elc49/vuno/Server/src/postgres/db"
	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/postgres"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "github.com/lib/pq"
	log "github.com/sirupsen/logrus"
)

func Init(option config.Rdbms) *db.Queries {
	dbConn, err := sql.Open(option.Driver, option.Uri)
	if err != nil {
		log.WithError(err).Fatalln("postgres: Init()")
		return nil
	}

	if err := dbConn.Ping(); err != nil {
		log.WithError(err).Fatalln("postgres: dbConn.Ping()")
		return nil
	} else if err == nil {
		log.Infoln("Postgres connection...OK")
	}

	dbConn.Exec(fmt.Sprintf("CREATE EXTENSION IF NOT EXISTS %q;", "uuid-ossp"))
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis;")
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis_rasters; --OPTIONAL")
	dbConn.Exec("CREATE EXTENSION IF NOT EXISTS postgis_topology; --OPTIONAL")

	// Setup postgres tables schema
	if err := runMigration(option.MigrationFile, option.Migrate, dbConn); err != nil {
		log.WithError(err).Fatalln("postgres: runMigration()")
	} else if err == nil {
		log.Infoln("Postgres tables schema...OK")
	}

	dB := db.New(dbConn)

	return dB
}

func runMigration(migrationFile string, migrateTables bool, conn *sql.DB) error {
	driver, err := postgres.WithInstance(conn, &postgres.Config{})
	if err != nil {
		return err
	}

	m, err := migrate.NewWithDatabaseInstance(migrationFile, "postgres", driver)
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
