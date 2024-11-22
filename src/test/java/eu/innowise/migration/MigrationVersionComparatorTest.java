package eu.innowise.migration;

import eu.innowise.model.Migration;
import eu.innowise.utils.MigrationTestData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MigrationVersionComparatorTest {

    private final MigrationVersionComparator comparator = new MigrationVersionComparator();

    @Test
    void testCompareEqualVersions() {
        // given
        Migration migration1 = MigrationTestData.builder().build().getMigration();
        Migration migration2 = MigrationTestData.builder().build().getMigration();

        // when
        int result = comparator.compare(migration1, migration2);

        // then
        assertEquals(0, result, "Versions should be equal");
    }

    @Test
    void testCompareFirstVersionSmaller() {
        // given
        Migration migration1 = MigrationTestData.builder()
                .withVersion("1.0")
                .build()
                .getMigration();

        Migration migration2 = MigrationTestData.builder()
                .withVersion("1.1")
                .build()
                .getMigration();

        // when
        int result = comparator.compare(migration1, migration2);

        // then
        assertEquals(-1, result, "Version 1.0 should be smaller than 1.1");
    }

    @Test
    void testCompareFirstVersionLarger() {
        // given
        Migration migration1 = MigrationTestData.builder()
                .withVersion("2.0")
                .build()
                .getMigration();

        Migration migration2 = MigrationTestData.builder()
                .withVersion("1.9")
                .build()
                .getMigration();

        // when
        int result = comparator.compare(migration1, migration2);

        // then
        assertEquals(1, result, "Version 2.0 should be larger than 1.9");
    }

    @Test
    void testCompareWithDifferentLengthVersions() {
        // given
        Migration migration1 = MigrationTestData.builder()
                .withVersion("1.1")
                .build()
                .getMigration();

        Migration migration2 = MigrationTestData.builder()
                .withVersion("1")
                .build()
                .getMigration();

        // when
        int result = comparator.compare(migration1, migration2);

        // then
        assertEquals(1, result, "Version 1.0.1 should be larger than 1.0");
    }

    @Test
    void testCompareWithTrailingZeros() {
        // given
        Migration migration1 = MigrationTestData.builder()
                .withVersion("1")
                .build()
                .getMigration();

        Migration migration2 = MigrationTestData.builder()
                .withVersion("1.0")
                .build()
                .getMigration();

        // when
        int result = comparator.compare(migration1, migration2);

        // then
        assertEquals(-1, result, "Version 1 should be smaller than 1.0");
    }

    @Test
    void testSortListOfMigrations() {
        // given
        List<Migration> migrations = Arrays.asList(
                MigrationTestData.builder().withVersion("1.2").build().getMigration(),
                MigrationTestData.builder().withVersion("1").build().getMigration(),
                MigrationTestData.builder().withVersion("2.1").build().getMigration(),
                MigrationTestData.builder().withVersion("2").build().getMigration()
        );

        // when
        migrations.sort(comparator);

        // then
        assertEquals("1", migrations.get(0).getVersion());
        assertEquals("1.2", migrations.get(1).getVersion());
        assertEquals("2", migrations.get(2).getVersion());
        assertEquals("2.1", migrations.get(3).getVersion());
    }
}