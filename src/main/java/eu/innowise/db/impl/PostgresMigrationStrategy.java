package eu.innowise.db.impl;

import eu.innowise.db.ConnectionManager;
import eu.innowise.db.MigrationStrategy;
import eu.innowise.exceptions.MigrationException;
import eu.innowise.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class PostgresMigrationStrategy implements MigrationStrategy {

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

