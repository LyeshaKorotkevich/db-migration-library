package eu.innowise.migration;

import eu.innowise.db.ConnectionManager;
import eu.innowise.exceptions.MigrationException;
import eu.innowise.exceptions.MigrationFileReadException;
import eu.innowise.model.AppliedMigration;
import eu.innowise.model.Migration;
import eu.innowise.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MigrationManager {

    private final MigrationFileReader fileReader;

    public List<AppliedMigration> getAppliedMigrations() {
        List<AppliedMigration> appliedMigrations = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(Constants.SELECT_FROM_SCHEMA_HISTORY)) {

            while (rs.next()) {
                String version = rs.getString("version");
                String description = rs.getString("description");
                int checksum = rs.getInt("checksum");
                LocalDateTime installedOn = rs.getTimestamp("installed_on").toLocalDateTime();

                appliedMigrations.add(new AppliedMigration(version, description, checksum, installedOn));
            }
        } catch (SQLException e) {
            log.error("Failed to fetch applied migrations and checksums.", e);
            throw new MigrationException("Failed to fetch applied migrations and checksums", e);
        }
        return appliedMigrations;
    }

    public List<Migration> getPendingMigrations() {
        List<Migration> allMigrations;
        try {
            allMigrations = fileReader.findMigrationFilesInResources();
        } catch (IOException | URISyntaxException e) {
            log.error("Error discovering migration files.", e);
            throw new MigrationException("Error discovering migration files.", e);
        }

        List<AppliedMigration> appliedMigrations = getAppliedMigrations();

        return allMigrations.stream()
                .filter(migration -> {
                    boolean isApplied = appliedMigrations.stream()
                            .anyMatch(appliedMigration -> appliedMigration.getVersion().equals(migration.getVersion()));

                    if (isApplied) {
                        AppliedMigration appliedMigration = appliedMigrations.stream()
                                .filter(applied -> applied.getVersion().equals(migration.getVersion()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Applied migration not found"));

                        int currentChecksum = migration.getChecksum();
                        int appliedChecksum = appliedMigration.getChecksum();

                        if (currentChecksum != appliedChecksum) {
                            log.warn("Migration with version {} has been modified. Skipping execution.", migration.getVersion());
                            throw new IllegalStateException("Migration has been modified: " + migration.getVersion());
                        }
                    }

                    return !isApplied;
                })
                .sorted(new MigrationVersionComparator())
                .toList();
    }
}