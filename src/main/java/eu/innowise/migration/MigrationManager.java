package eu.innowise.migration;

import eu.innowise.utils.ConnectionManager;
import eu.innowise.utils.Constants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MigrationManager {

    private static final String MIGRATION_FILE_PATTERN = "^V(\\d+)__.*\\.sql$";
    private static final String CREATE_SCHEMA_TABLE = """
            CREATE TABLE IF NOT EXISTS schema_history (
                installed_rank SERIAL PRIMARY KEY,
                version INT NOT NULL,
                description VARCHAR(200) NOT NULL,
                checksum INT,
                success BOOLEAN NOT NULL,
                installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

    private final MigrationFileReader fileReader;

    public MigrationManager(MigrationFileReader fileReader) {
        this.fileReader = fileReader;
    }

    public void ensureMetadataTableExists() throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement()) {
                stmt.execute(CREATE_SCHEMA_TABLE);
        }
    }

    public List<Integer> getAppliedMigrations() {
        List<Integer> appliedMigrations = new ArrayList<>();
        String query = "SELECT version FROM " + Constants.SCHEMA_HISTORY_TABLE + " WHERE success = TRUE";

        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                appliedMigrations.add(rs.getInt("version"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch applied migrations", e);
        }

        return appliedMigrations;
    }

    public List<Path> getPendingMigrations() {
        List<Path> allMigrations;
        try {
            allMigrations = fileReader.findMigrationFilesInResources();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        List<Integer> appliedMigrations = getAppliedMigrations();

        List<Path> pendingMigrations = new ArrayList<>();
        for (Path migration : allMigrations) {
            int version = extractVersionFromFilename(migration.getFileName().toString());
            if (!appliedMigrations.contains(version)) {
                pendingMigrations.add(migration);
            }
        }

        return pendingMigrations;
    }

    public int extractVersionFromFilename(String filename) {
        Pattern pattern = Pattern.compile(MIGRATION_FILE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid migration filename: " + filename);
    }
}
