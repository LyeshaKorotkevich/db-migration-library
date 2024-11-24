package eu.innowise;

import eu.innowise.db.ConnectionManager;
import eu.innowise.db.MigrationStrategy;
import eu.innowise.db.MigrationStrategyFactory;
import eu.innowise.exceptions.MigrationException;
import eu.innowise.migration.MigrationExecutor;
import eu.innowise.migration.MigrationFileReader;
import eu.innowise.migration.MigrationManager;
import eu.innowise.model.AppliedMigration;
import eu.innowise.model.Migration;
import eu.innowise.utils.DatabaseUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MigrationTool {

    public static void migrate() {
        log.info("Starting migrations...");

        String dbType = DatabaseUtils.getDatabaseType();
        MigrationStrategy strategy = MigrationStrategyFactory.getMigrationStrategy(dbType);


        MigrationFileReader fileReader = new MigrationFileReader();
        MigrationManager migrationManager = new MigrationManager(fileReader);
        MigrationExecutor migrationExecutor = new MigrationExecutor(migrationManager, fileReader);

        try {
            log.debug("Ensuring metadata table exists...");
            strategy.ensureMetadataTableExists();
            log.info("Metadata table check/creation completed successfully.");

            List<Migration> migrations = migrationManager.getPendingMigrations();
            log.info("Found {} pending migrations: {}", migrations.size(), migrations);

            if (!migrations.isEmpty()) {
                migrationExecutor.executeMigrations(migrations);
            } else {
                log.info("No pending migrations found.");
            }

            log.info("Migrations completed successfully.");

        } catch (Exception e) {
            log.error("Error during migration.", e);
            throw new MigrationException("Migration process failed.", e);

        } finally {
            ConnectionManager.closeDataSource();
        }
    }

    public static void rollback(String version) {
        MigrationFileReader fileReader = new MigrationFileReader();
        MigrationManager migrationManager = new MigrationManager(fileReader);
        MigrationExecutor migrationExecutor = new MigrationExecutor(migrationManager, fileReader);

        try {
            migrationExecutor.rollbackMigrationToVersion(version);
        } catch (Exception e) {
            log.error("Error during rollback.", e);
            throw new MigrationException("Rollback failed.", e);

        } finally {
            ConnectionManager.closeDataSource();
        }
    }

    public static void showStatus() {
        try {
            MigrationManager migrationManager = new MigrationManager(new MigrationFileReader());
            List<AppliedMigration> appliedMigrations = migrationManager.getAppliedMigrations();

            if (appliedMigrations.isEmpty()) {
                System.out.println("No migrations have been applied yet.");
                return;
            }

            System.out.println("\nDATABASE MIGRATION STATUS");
            System.out.println("==========================");
            System.out.printf("%-10s %-30s %-10s %-20s%n", "Version", "Description", "Checksum", "Installed On");
            System.out.println("--------------------------------------------------------------------------------");

            for (AppliedMigration migration : appliedMigrations) {
                System.out.printf("%-10s %-30s %-10d %-20s%n",
                        migration.getVersion(),
                        migration.getDescription(),
                        migration.getChecksum(),
                        migration.getInstalledOn());
            }

            String currentVersion = appliedMigrations.get(appliedMigrations.size() - 1).getVersion();
            System.out.println("\nCurrent version: " + currentVersion);

            log.info("Status command executed successfully.");
        } catch (Exception e) {
            log.error("Failed to retrieve migration status.", e);
            System.err.println("An error occurred while retrieving migration status. Check logs for details.");
        }
    }
}