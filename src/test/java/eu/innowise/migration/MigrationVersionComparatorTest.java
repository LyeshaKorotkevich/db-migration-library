package eu.innowise.migration;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MigrationVersionComparatorTest {

    private final MigrationVersionComparator comparator = new MigrationVersionComparator();

    @Test
    void testSortMigrations() {

        List<Path> migrations = Arrays.asList(
                Paths.get("V1.2__SecondMigration.sql"),
                Paths.get("V1_10__ThirdMigration.sql"),
                Paths.get("V2.0__FourthMigration.sql"),
                Paths.get("V1__FirstMigration.sql")
        );

        migrations.sort(comparator);

        assertEquals("V1__FirstMigration.sql", migrations.get(0).getFileName().toString());
        assertEquals("V1.2__SecondMigration.sql", migrations.get(1).getFileName().toString());
        assertEquals("V1_10__ThirdMigration.sql", migrations.get(2).getFileName().toString());
        assertEquals("V2.0__FourthMigration.sql", migrations.get(3).getFileName().toString());
    }
}