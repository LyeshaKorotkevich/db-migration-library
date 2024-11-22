package eu.innowise.migration;

import eu.innowise.db.ConnectionManager;
import eu.innowise.model.Migration;
import eu.innowise.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MigrationExecutor {

    public void executeMigrations(List<Migration> migrations) {
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

    private void executeSingleMigration(Connection connection, Migration migration) throws SQLException, IOException {

        log.info("Starting migration for file: {}", migration.getDescription());
        log.debug("Migration version: {}, checksum: {}", migration.getVersion(), migration.getChecksum());

        List<String> sqlStatements = migration.getSqlStatements();
        for (String sql : sqlStatements) {
            log.debug("Executing SQL: {}", sql);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                log.error("Found error in migration with version: {}", migration.getVersion());
                throw e;
            }
        }

        insertSchemaHistory(connection, migration);
        log.info("Migration completed successfully for file: {}", migration.getDescription());
    }

    private void insertSchemaHistory(Connection connection, Migration migration) {
        try (PreparedStatement stmt = connection.prepareStatement(Constants.INSERT_SCHEMA_HISTORY)) {
            stmt.setString(1, migration.getVersion());
            stmt.setString(2, migration.getDescription());
            stmt.setInt(3, migration.getChecksum());
            stmt.executeUpdate();
            log.info("Schema history updated for version: {}, ", migration.getVersion());
        } catch (SQLException e) {
            log.error("Failed to update schema history for version: {}", migration.getVersion(), e);
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