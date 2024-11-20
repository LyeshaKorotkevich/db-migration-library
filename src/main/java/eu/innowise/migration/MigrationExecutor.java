package eu.innowise.migration;

import eu.innowise.utils.ConnectionManager;
import eu.innowise.utils.Constants;
import eu.innowise.utils.MigrationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MigrationExecutor {

    private final MigrationFileReader fileReader;

    public void executeMigrations(List<Path> migrationFiles) {
        log.info("Starting batch migration for {} files.", migrationFiles.size());

        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                lockSchemaHistoryTable(connection);

                for (Path migrationFile : migrationFiles) {
                    executeSingleMigration(connection, migrationFile);
                }

                connection.commit();
                log.info("All migrations completed successfully.");
            } catch (Exception e) {
                log.error("Batch migration failed. Rolling back all changes.", e);
                try {
                    connection.rollback();
                } catch (SQLException exception) {
                    log.error("Error during rollback.");
                }
                throw new RuntimeException("Batch migration process failed.", e);
            }
        } catch (SQLException e) {
            log.error("Database connection error during migration.", e);
            throw new RuntimeException("Error during batch migration process.", e);
        }
    }

    private void executeSingleMigration(Connection connection, Path migrationFile) throws SQLException, IOException {
        String description = migrationFile.getFileName().toString();
        String version = MigrationManager.extractVersionFromFilename(description);
        int checksum = MigrationUtils.calculateChecksum(migrationFile);

        log.info("Starting migration for file: {}", description);
        log.debug("Migration version: {}, checksum: {}", version, checksum);

        List<String> sqlStatements = fileReader.parseSqlFile(migrationFile);
        for (String sql : sqlStatements) {
            log.debug("Executing SQL: {}", sql);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                log.error("Found error in migration with version: {}", version);
                throw e;
            }
        }

        insertSchemaHistory(connection, version, description, checksum);
        log.info("Migration completed successfully for file: {}", description);
    }

    private void insertSchemaHistory(Connection connection, String version, String description, int checksum) {
        try (PreparedStatement stmt = connection.prepareStatement(Constants.INSERT_SCHEMA_HISTORY)) {
            stmt.setString(1, version);
            stmt.setString(2, description);
            stmt.setInt(3, checksum);
            stmt.executeUpdate();
            log.info("Schema history updated for version: {}, ", version);
        } catch (SQLException e) {
            log.error("Failed to update schema history for version: {}", version, e);
            throw new RuntimeException("Failed to update schema history", e);
        }
    }

    private void lockSchemaHistoryTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            log.info("Acquiring lock on schema history table...");
            stmt.executeQuery(Constants.SELECT_SCHEMA_HISTORY_FOR_UPDATE);
            log.info("Lock acquired on schema history table.");
        } catch (SQLException e) {
            log.error("Error acquiring lock on schema history table", e);
            throw e;
        }
    }
}