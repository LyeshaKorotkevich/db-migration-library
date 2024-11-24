package eu.innowise.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Abstract base class representing a migration with a version, description, and checksum.
 */
@Getter
@RequiredArgsConstructor
public abstract class BaseMigration {

    private final String version;
    private final String description;
    private final int checksum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseMigration that = (BaseMigration) o;
        return version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return version.hashCode();
    }
}
