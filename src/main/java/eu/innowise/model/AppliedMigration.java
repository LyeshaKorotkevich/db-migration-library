package eu.innowise.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Represents an applied migration in the database.
 * This class extends {@link BaseMigration} and includes the timestamp of when the migration was applied.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class AppliedMigration extends BaseMigration {

    private final LocalDateTime installedOn;

    public AppliedMigration(String version, String description, int checksum, LocalDateTime installedOn) {
        super(version, description, checksum);
        this.installedOn = installedOn;
    }
}
