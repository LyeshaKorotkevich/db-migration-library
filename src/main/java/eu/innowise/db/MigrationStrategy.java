package eu.innowise.db;

public interface MigrationStrategy {

    void ensureMetadataTableExists();
}
