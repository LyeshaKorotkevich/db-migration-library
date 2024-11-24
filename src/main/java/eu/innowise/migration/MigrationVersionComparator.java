package eu.innowise.migration;

import eu.innowise.model.BaseMigration;

import java.util.Comparator;

/**
 * Comparator for comparing migrations across versions.
 * This comparator compares two {@link BaseMigration} objects based on their version strings.
 */
public class MigrationVersionComparator implements Comparator<BaseMigration> {

    /**
     * Compares two {@link BaseMigration} objects based on their version.
     *
     * @param migration1 the first migration to compare
     * @param migration2 the second migration to compare
     * @return a negative integer, zero, or a positive integer if the version of {@code migration1} is less than, equal to, or greater than the version of {@code migration2}
     */
    @Override
    public int compare(BaseMigration migration1, BaseMigration migration2) {
        String version1 = migration1.getVersion();
        String version2 = migration2.getVersion();
        return compareVersions(version1, version2);
    }

    /**
     * Compares two version strings in a dot-separated numeric format.
     * For example, "1.2" will be compared with "1.3".
     *
     * <p>Versions are compared by each part of the version string, starting from the left-most part.
     * If the versions have different lengths, the shorter version is considered smaller.</p>
     *
     * @param version1 the first version to compare
     * @param version2 the second version to compare
     * @return a negative integer, zero, or a positive integer if {@code version1} is less than, equal to, or greater than {@code version2}
     */
    public static int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int length = Math.min(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int part1 = Integer.parseInt(parts1[i]);
            int part2 = Integer.parseInt(parts2[i]);
            if (part1 != part2) {
                return part1 - part2;
            }
        }
        return parts1.length - parts2.length;
    }
}
