package eu.innowise.migration;

import eu.innowise.db.ConnectionManager;
import eu.innowise.utils.Constants;
import eu.innowise.utils.MigrationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MigrationManager {

    private final MigrationFileReader fileReader;

    public Map<String, Integer> getAppliedMigrationsWithChecksums() {
        Map<String, Integer> appliedMigrations = new HashMap<>();
        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(Constants.SELECT_VERSIONS_AND_CHECKSUMS_FROM_SCHEMA_HISTORY)) {

            while (rs.next()) {
                String version = rs.getString("version");
                int checksum = rs.getInt("checksum");
                appliedMigrations.put(version, checksum);
            }
        } catch (SQLException e) {
            log.error("Failed to fetch applied migrations and checksums.", e);
            throw new RuntimeException("Failed to fetch applied migrations and checksums", e);
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

        Map<String, Integer> appliedMigrations = getAppliedMigrationsWithChecksums();

        return allMigrations.stream()
                .filter(migration -> {
                    String version = MigrationUtils.extractVersionFromFilename(migration.getFileName().toString());
                    if (appliedMigrations.containsKey(version)) {
                        int currentChecksum = MigrationUtils.calculateChecksum(migration);
                        int appliedChecksum = appliedMigrations.get(version);
                        if (currentChecksum != appliedChecksum) {
                            log.warn("Migration with version {} has been modified. Skipping execution.", version);
                            throw new IllegalStateException("Migration has been modified: " + version);
                        }
                    }
                    return !appliedMigrations.containsKey(version);
                })
                .sorted(new MigrationVersionComparator())
                .toList();
    }
}