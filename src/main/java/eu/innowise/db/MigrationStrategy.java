package eu.innowise.db;

/**
 * Strategy interface for database migration.
 */
public interface MigrationStrategy {

    void ensureMetadataTableExists();
}
