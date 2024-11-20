package eu.innowise.migration;

import eu.innowise.utils.ConnectionManager;
import eu.innowise.utils.Constants;
import eu.innowise.utils.MigrationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class MigrationManager {


    private final MigrationFileReader fileReader;

    public void ensureMetadataTableExists() throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement()) {
                stmt.execute(Constants.CREATE_SCHEMA_TABLE);
        } catch (SQLException e) {
            log.error("Failed to ensure schema metadata table.", e);
            throw e;
        }
    }

    public List<String> getAppliedMigrations() {
        List<String> appliedMigrations = new ArrayList<>();

        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(Constants.SELECT_VERSIONS_FROM_SCHEMA_HISTORY)) {

            while (rs.next()) {
                appliedMigrations.add(rs.getString("version"));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch applied migrations.", e);
            throw new RuntimeException("Failed to fetch applied migrations", e);
        }

        return appliedMigrations;
    }

    public List<Path> getPendingMigrations() {
        List<Path> allMigrations;
        try {
            allMigrations = fileReader.findMigrationFilesInResources();
        } catch (IOException | URISyntaxException e) {
            log.error("Error discovering migration files.", e);
            throw new RuntimeException(e);
        }

        List<String> appliedMigrations = getAppliedMigrations();

        return allMigrations.stream()
                .filter(migration -> {
                    String version = extractVersionFromFilename(migration.getFileName().toString());
                    if (appliedMigrations.contains(version)) {
                        try {
                            if (isMigrationChanged(migration, version)) {
                                log.warn("Migration {} has been modified. Skipping execution.", version);
                                throw new IllegalStateException("Migration has been modified: " + version);
                            }
                        } catch (SQLException e) {
                            log.error("Error checking if migration was modified.", e);
                            throw new RuntimeException("Error checking if migration was modified", e);
                        }
                    }
                    return !appliedMigrations.contains(version);
                })
                .sorted(new MigrationVersionComparator())
                .toList();
    }

    public static String extractVersionFromFilename(String filename) {
        Pattern pattern = Pattern.compile(Constants.MIGRATION_FILE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(1).replace("_", ".");
        }
        log.error("Invalid migration filename: {}", filename);
        throw new IllegalArgumentException("Invalid migration filename: " + filename);
    }

    private boolean isMigrationChanged(Path migrationFile, String appliedVersion) throws SQLException {
        int currentChecksum = MigrationUtils.calculateChecksum(migrationFile);
        int appliedChecksum = getAppliedMigrationChecksum(appliedVersion);
        return currentChecksum != appliedChecksum;
    }

    private int getAppliedMigrationChecksum(String version) throws SQLException {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(Constants.SELECT_CHECKSUM_FROM_SCHEMA_HISTORY)) {
            stmt.setString(1, version);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("checksum");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            log.error("Failed to fetch checksum for migration version: {}", version, e);
            throw new RuntimeException("Failed to fetch checksum for migration version", e);
        }
    }
}