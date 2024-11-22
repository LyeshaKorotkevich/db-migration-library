package eu.innowise.migration;

import eu.innowise.model.Migration;

import java.util.Comparator;

public class MigrationVersionComparator implements Comparator<Migration> {

    @Override
    public int compare(Migration migration1, Migration migration2) {
        String version1 = migration1.getVersion();
        String version2 = migration2.getVersion();
        return compareVersions(version1, version2);
    }

    private int compareVersions(String version1, String version2) {
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
