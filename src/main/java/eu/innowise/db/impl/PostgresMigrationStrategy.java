package eu.innowise.db.impl;

import eu.innowise.db.ConnectionManager;
import eu.innowise.db.MigrationStrategy;
import eu.innowise.exceptions.MigrationException;
import eu.innowise.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementation of the MigrationStrategy for PostgreSQL database.
 * Ensures that the metadata table exists in the PostgreSQL database.
 */
@Slf4j
public class PostgresMigrationStrategy implements MigrationStrategy {

    /**
     * Ensures that the schema metadata table exists in the PostgreSQL database.
     * If the table doesn't exist, it will be created.
     *
     * @throws MigrationException if there is an error while creating the table
     */
    @Override
    public void ensureMetadataTableExists() {
        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(Constants.CREATE_SCHEMA_TABLE_PG);
        } catch (SQLException e) {
            log.error("Failed to ensure schema metadata table.", e);
            throw new MigrationException("Failed to ensure schema metadata table.", e);
        }
    }
}

