package eu.innowise.migration;

import eu.innowise.utils.ConnectionManager;
import eu.innowise.utils.Constants;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MigrationExecutor {

    private final MigrationFileReader fileReader;
    private final MigrationManager migrationManager;

    public MigrationExecutor(MigrationFileReader fileReader, MigrationManager migrationManager) {
        this.fileReader = fileReader;
        this.migrationManager = migrationManager;
    }

    public void executeMigration(Path migrationFile) {
        String description = migrationFile.getFileName().toString();
        int version = migrationManager.extractVersionFromFilename(description);
        int checksum = calculateChecksum(migrationFile);

        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                List<String> sqlStatements = fileReader.parseSqlFile(migrationFile);
                for (String sql : sqlStatements) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(sql);
                    }
                }

                insertSchemaHistory(connection, version, description, checksum, true);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                insertSchemaHistory(connection, version, description, checksum, false);
                throw new RuntimeException("Migration failed for file: " + migrationFile.getFileName(), e);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Error applying migration", e);
        }
    }

    private void insertSchemaHistory(Connection connection, int version, String description, int checksum, boolean success) {
        String sql = "INSERT INTO " + Constants.SCHEMA_HISTORY_TABLE +
                " (version, description, checksum, success) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, version);
            stmt.setString(2, description);
            stmt.setInt(3, checksum);
            stmt.setBoolean(4, success);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update schema history", e);
        }
    }

    private int calculateChecksum(Path file) {
        try (InputStream inputStream = Files.newInputStream(file)) {
            String md5Hex = DigestUtils.md5Hex(inputStream);
            return md5Hex.hashCode();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate checksum for file: " + file, e);
        }
    }
}
