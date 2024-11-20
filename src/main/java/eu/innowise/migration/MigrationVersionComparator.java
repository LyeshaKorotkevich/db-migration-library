package eu.innowise.migration;

import java.nio.file.Path;
import java.util.Comparator;

public class MigrationVersionComparator implements Comparator<Path> {

    @Override
    public int compare(Path path1, Path path2) {
        String version1 = MigrationManager.extractVersionFromFilename(path1.getFileName().toString());
        String version2 = MigrationManager.extractVersionFromFilename(path2.getFileName().toString());
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
