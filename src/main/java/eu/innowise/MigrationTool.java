package eu.innowise;

import eu.innowise.db.ConnectionManager;
import eu.innowise.db.MigrationStrategy;
import eu.innowise.db.MigrationStrategyFactory;
import eu.innowise.exceptions.MigrationException;
import eu.innowise.migration.MigrationExecutor;
import eu.innowise.migration.MigrationFileReader;
import eu.innowise.migration.MigrationManager;
import eu.innowise.model.Migration;
import eu.innowise.utils.DatabaseUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MigrationTool {

    public static void run() {
        log.info("Starting migrations...");

        String dbType = DatabaseUtils.getDatabaseType();
        MigrationStrategy strategy = MigrationStrategyFactory.getMigrationStrategy(dbType);


        MigrationFileReader fileReader = new MigrationFileReader();
        MigrationManager migrationManager = new MigrationManager(fileReader);
        MigrationExecutor migrationExecutor = new MigrationExecutor();

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
}