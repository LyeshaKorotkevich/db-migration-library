package eu.innowise;

import eu.innowise.migration.MigrationExecutor;
import eu.innowise.migration.MigrationFileReader;
import eu.innowise.migration.MigrationManager;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class MigrationTool {

    public static void main(String[] args) {
        log.info("Starting migrations...");

        MigrationFileReader fileReader = new MigrationFileReader();
        MigrationManager migrationManager = new MigrationManager(fileReader);
        MigrationExecutor migrationExecutor = new MigrationExecutor(fileReader);

        try {
            log.debug("Ensuring metadata table exists...");
            migrationManager.ensureMetadataTableExists();
            log.info("Metadata table check/creation completed successfully.");
        } catch (SQLException e) {
            log.error("Failed to ensure metadata table exists.", e);
            throw new RuntimeException("Error ensuring metadata table exists.", e);
        }

        List<Path> migrations = migrationManager.getPendingMigrations();
        log.info("Found {} pending migrations: {}", migrations.size(), migrations);

        if (!migrations.isEmpty()) {
            migrationExecutor.executeMigrations(migrations);
        } else {
            log.info("No pending migrations found.");
        }

        log.info("Migrations completed successfully.");
    }
}