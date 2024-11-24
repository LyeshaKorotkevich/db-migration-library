package eu.innowise.migration;

import eu.innowise.db.ConnectionManager;
import eu.innowise.exceptions.MigrationException;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class responsible for handling migration operations such as getting applied migrations and
 * determining pending migrations.
 */
@Slf4j
@RequiredArgsConstructor
public class MigrationManager {

    private final MigrationFileReader fileReader;

    /**
     * Retrieves a list of applied migrations from the database.
     *
     * @return a list of {@link AppliedMigration} objects representing the migrations that have already been applied
     * @throws MigrationException if there is an error getting applied migrations from the database
     */
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

    /**
     * Retrieves the list of pending migrations that have not been applied yet.
     * This method checks for migrations in the resources folder and compares them with
     * the applied migrations stored in the database. If the migration has been applied,
     * it is skipped. If the migration's checksum has changed, it is stopped.
     *
     * @return a list of {@link Migration} objects representing the migrations that are pending application
     * @throws MigrationException if there is an error discovering migration files or comparing them with applied migrations
     */
    public List<Migration> getPendingMigrations() {
        List<Migration> allMigrations = loadAllMigrations();
        List<AppliedMigration> appliedMigrations = getAppliedMigrations();

        // Creating map to find faster with versions
        var appliedMigrationsMap = appliedMigrations.stream()
                .collect(Collectors.toMap(AppliedMigration::getVersion, appliedMigration -> appliedMigration));

        return allMigrations.stream()
                .filter(migration -> isPendingMigration(migration, appliedMigrationsMap))
                .sorted(new MigrationVersionComparator())
                .toList();
    }

    private List<Migration> loadAllMigrations() {
        try {
            return fileReader.findMigrationFilesInResources();
        } catch (IOException | URISyntaxException e) {
            log.error("Error discovering migration files.", e);
            throw new MigrationException("Error discovering migration files.", e);
        }
    }

    private boolean isPendingMigration(Migration migration, Map<String, AppliedMigration> appliedMigrationsMap) {
        AppliedMigration appliedMigration = appliedMigrationsMap.get(migration.getVersion());

        if (appliedMigration != null) {
            if (hasChecksumChanged(migration, appliedMigration)) {
                log.warn("Migration with version {} has been modified. Skipping execution.", migration.getVersion());
                throw new IllegalStateException("Migration has been modified: " + migration.getVersion());
            }
            return false;
        }

        return true;
    }

    private boolean hasChecksumChanged(Migration migration, AppliedMigration appliedMigration) {
        return migration.getChecksum() != appliedMigration.getChecksum();
    }
}