package eu.innowise.migration;

import eu.innowise.db.ConnectionManager;
import eu.innowise.exceptions.MigrationException;
import eu.innowise.exceptions.SchemaLockException;
import eu.innowise.model.AppliedMigration;
import eu.innowise.model.Migration;
import eu.innowise.report.MigrationReportGenerator;
import eu.innowise.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Class is responsible for executing and rolling back database migrations.
*/
@Slf4j
@RequiredArgsConstructor
public class MigrationExecutor {

    private final MigrationManager migrationManager;
    private final MigrationFileReader fileReader;

    /**
     * Executes migrations on the database.
     *
     * @param migrations The list of migrations to be executed.
     * @throws MigrationException if an error occurs during migration execution.
     */
    public void executeMigrations(List<Migration> migrations) throws MigrationException {
        log.info("Starting batch migration for {} files.", migrations.size());

        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                lockSchemaHistoryTable(connection);

                for (Migration migration : migrations) {
                    executeSingleMigration(connection, migration);
                }

                connection.commit();
                log.info("All migrations completed successfully.");
                MigrationReportGenerator.generateJsonReport(migrations, true);
            } catch (Exception e) {
                log.error("Batch migration failed. Rolling back all changes.", e);
                rollbackTransaction(connection);
                throw new MigrationException("Batch migration process failed.", e);
            }
        } catch (SQLException e) {
            log.error("Database connection error during migration.", e);
            throw new MigrationException("Error during batch migration process.", e);
        }
    }

    /**
     * Executes the rollback of migrations to the specified version.
     * This method goes through the list of applied migrations and rolls them back
     * in reverse order (starting from the most recent one), until the specified
     * target version is reached. All SQL rollback statements are executed,
     * and schema history is updated accordingly. If an error occurs,
     * the transaction is rolled back.
     *
     * @param targetVersion the version to which migrations should be rolled back
     * @throws MigrationException if there is an error executing the rollback or updating schema history
     */
    public void rollbackMigrationToVersion(String targetVersion) throws MigrationException {
        log.info("Starting rollback to version: {}", targetVersion);

        List<AppliedMigration> appliedMigrations = migrationManager.getAppliedMigrations();

        List<AppliedMigration> migrationsToRollback = appliedMigrations.stream()
                .filter(m -> MigrationVersionComparator
                        .compareVersions(m.getVersion(), targetVersion) > 0)
                .sorted(new MigrationVersionComparator().reversed())
                .toList();

        if (migrationsToRollback.isEmpty()) {
            log.info("No migrations to rollback.");
            return;
        }

        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                lockSchemaHistoryTable(connection);

                for (AppliedMigration migration : migrationsToRollback) {
                    rollbackSingleMigration(connection, migration);
                }

                connection.commit();
                log.info("Rollback completed successfully.");
            } catch (Exception e) {
                log.error("Rollback failed. Rolling back all changes.", e);
                rollbackTransaction(connection);
                throw new MigrationException("Rolling back process failed.", e);
            }
        } catch (Exception e) {
            log.error("Rollback failed. Rolling back all changes.", e);
            throw new MigrationException("Rollback process failed.", e);
        }
    }

    private void executeSingleMigration(Connection connection, Migration migration) throws MigrationException {

        log.info("Starting migration for file: {}", migration.getDescription());
        log.debug("Migration version: {}, checksum: {}", migration.getVersion(), migration.getChecksum());

        List<String> sqlStatements = migration.getSqlStatements();
        for (String sql : sqlStatements) {
            log.debug("Executing SQL: {}", sql);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                log.error("Found error in migration with version: {}", migration.getVersion());
                MigrationReportGenerator.generateJsonReport(migration, false);
                throw new MigrationException("Found error in migration file", e);
            }
        }

        insertSchemaHistory(connection, migration);
        log.info("Migration completed successfully for file: {}", migration.getDescription());
    }

    private void rollbackSingleMigration(Connection connection, AppliedMigration appliedMigration) throws MigrationException {
        log.info("Rolling back migration: {}", appliedMigration.getDescription());

        try {
            List<Migration> rollbackFiles = fileReader.findRollbackFilesInResources();
            Migration rollbackMigration = rollbackFiles.stream()
                    .filter(m -> m.getVersion().equals(appliedMigration.getVersion()))
                    .findFirst()
                    .orElseThrow(() -> new MigrationException(
                            "No rollback file found for migration version: " + appliedMigration.getVersion()));

            log.info("Found rollback file for version: {}", appliedMigration.getVersion());

            List<String> rollbackStatements = rollbackMigration.getSqlStatements();
            if (rollbackStatements.isEmpty()) {
                log.warn("No rollback SQL statements found for version: {}", appliedMigration.getVersion());
                throw new MigrationException("Rollback file is empty or contains no SQL statements.");
            }

            for (String sql : rollbackStatements) {
                log.debug("Executing rollback SQL: {}", sql);
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    log.error("Error executing rollback SQL for version: {}", appliedMigration.getVersion(), e);
                    throw new MigrationException("Error executing rollback SQL for version: " + appliedMigration.getVersion(), e);
                }
            }

            removeMigrationFromSchemaHistory(connection, appliedMigration);
            log.info("Rollback for version {} completed successfully.", appliedMigration.getVersion());

        } catch (IOException | URISyntaxException e) {
            log.error("Error reading rollback files.", e);
            throw new MigrationException("Error finding rollback files for migration: " + appliedMigration.getVersion(), e);
        }
    }


    private void removeMigrationFromSchemaHistory(Connection connection, AppliedMigration migration) throws MigrationException {
        try (PreparedStatement statement = connection.prepareStatement(Constants.DELETE_FROM_SCHEMA_HISTORY)) {
            statement.setString(1, migration.getVersion());
            statement.executeUpdate();
            log.info("Migration {} removed from schema history.", migration.getVersion());
        } catch (SQLException e) {
            log.error("Failed to remove migration from schema history for version: {}", migration.getVersion(), e);
            throw new MigrationException("Failed to remove migration from schema history", e);
        }
    }

    private void insertSchemaHistory(Connection connection, Migration migration) throws MigrationException {
        try (PreparedStatement statement = connection.prepareStatement(Constants.INSERT_SCHEMA_HISTORY)) {
            statement.setString(1, migration.getVersion());
            statement.setString(2, migration.getDescription());
            statement.setInt(3, migration.getChecksum());
            statement.executeUpdate();
            log.info("Schema history updated for version: {}, ", migration.getVersion());
        } catch (SQLException e) {
            log.error("Failed to update schema history for version: {}", migration.getVersion(), e);
            throw new MigrationException("Failed to update schema history", e);
        }
    }

    private void lockSchemaHistoryTable(Connection connection) throws SchemaLockException {
        try (Statement statement = connection.createStatement()) {
            log.info("Acquiring lock on schema history table...");
            statement.executeQuery(Constants.SELECT_SCHEMA_HISTORY_FOR_UPDATE);
            log.info("Lock acquired on schema history table.");
        } catch (SQLException e) {
            log.error("Error acquiring lock on schema history table", e);
            throw new SchemaLockException("Error acquiring lock on schema history table", e);
        }
    }

    private void rollbackTransaction(Connection connection) {
        try {
            connection.rollback();
            log.info("Rollback successful.");
        } catch (SQLException e) {
            log.error("Error during rollback.", e);
        }
    }
}