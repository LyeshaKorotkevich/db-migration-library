package eu.innowise.db.impl;

import eu.innowise.db.ConnectionManager;
import eu.innowise.db.MigrationStrategy;
import eu.innowise.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class MySQLMigrationStrategy implements MigrationStrategy {

    @Override
    public void ensureMetadataTableExists() throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(Constants.CREATE_SCHEMA_TABLE_MYSQL);
        } catch (SQLException e) {
            log.error("Failed to ensure schema metadata table.", e);
            throw e;
        }
    }
}
