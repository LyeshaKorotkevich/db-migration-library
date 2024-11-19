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
        System.out.println("Starting migrations...");
        MigrationFileReader fileReader = new MigrationFileReader();
        MigrationManager migrationManager = new MigrationManager(fileReader);
        MigrationExecutor migrationExecutor = new MigrationExecutor(fileReader, migrationManager);
        try {
            migrationManager.ensureMetadataTableExists();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Path> migrations = migrationManager.getPendingMigrations();
        System.out.println("Found migrations: " + migrations);

        for(Path migration: migrations) {
            migrationExecutor.executeMigration(migration);
        }
        System.out.println("Migrations completed successfully.");
    }
}