package eu.innowise;

import eu.innowise.db.ConnectionManager;
import eu.innowise.db.MigrationStrategy;
import eu.innowise.db.MigrationStrategyFactory;
import eu.innowise.migration.MigrationExecutor;
import eu.innowise.migration.MigrationFileReader;
import eu.innowise.migration.MigrationManager;
import eu.innowise.utils.DatabaseUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class MigrationTool {

    public static void run() {
        log.info("Starting migrations...");

        String dbType = DatabaseUtils.getDatabaseType();
        MigrationStrategy strategy = MigrationStrategyFactory.getMigrationStrategy(dbType);


        MigrationFileReader fileReader = new MigrationFileReader();
        MigrationManager migrationManager = new MigrationManager(fileReader);
        MigrationExecutor migrationExecutor = new MigrationExecutor(fileReader);

        try {
            log.debug("Ensuring metadata table exists...");
            strategy.ensureMetadataTableExists();
            log.info("Metadata table check/creation completed successfully.");

            List<Path> migrations = migrationManager.getPendingMigrations();
            log.info("Found {} pending migrations: {}", migrations.size(), migrations);

            if (!migrations.isEmpty()) {
                migrationExecutor.executeMigrations(migrations);
            } else {
                log.info("No pending migrations found.");
            }

            log.info("Migrations completed successfully.");

        } catch (SQLException e) {
            log.error("Error during migration.", e);
            throw new RuntimeException("Migration process failed.", e);

        } finally {
            ConnectionManager.closeDataSource();
        }
    }
}