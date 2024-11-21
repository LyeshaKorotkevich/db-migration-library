package eu.innowise.db;

import java.sql.SQLException;

public interface MigrationStrategy {

    void ensureMetadataTableExists() throws SQLException;
}
