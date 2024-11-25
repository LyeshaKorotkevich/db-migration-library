package eu.innowise.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class MigrationUtilsTest {

    @Nested
    class calculateChecksum {

        @Test
        void calculateChecksum_shouldReturnSameChecksum_whenFileIsNotChanged() throws IOException {
            // given
            Path file = Path.of("testFile.txt");
            Files.writeString(file, "test content");

            // when
            int checksum1 = MigrationUtils.calculateChecksum(file);
            int checksum2 = MigrationUtils.calculateChecksum(file);

            // then
            Assertions.assertEquals(checksum1, checksum2);
        }

        @Test
        void calculateChecksum_shouldBeDifferent_whenFileIsChanged() throws IOException {
            // given
            Path file = Path.of("testFile.txt");
            Files.writeString(file, "test content");
            int expected = MigrationUtils.calculateChecksum(file);

            // when
            Files.writeString(file, "new text");
            int actual = MigrationUtils.calculateChecksum(file);

            // then
            Assertions.assertNotEquals(expected, actual);
        }
    }

    @Nested
    class ExtractVersionFromFilename {

        @Test
        void extractVersionFromFilename_shouldReturnCorrectVersion_whenFilenameIsValid() {
            // given
            String filename = "V1_0__Initial_migration.sql";

            // when
            String version = MigrationUtils.extractVersionFromFilename(filename);

            // then
            Assertions.assertEquals("1.0", version);
        }

        @Test
        void extractVersionFromFilename_shouldThrowIllegalArgumentException_whenFilenameIsInvalid() {
            // given
            String filename = "V__Initial_migration.sql.sql";

            // when & then
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> MigrationUtils.extractVersionFromFilename(filename));
        }
    }

    @Nested
    class ExtractDescriptionFromFilename {

        @Test
        void extractDescriptionFromFilename_shouldReturnCorrectDescription_whenFilenameIsValid() {
            // given
            String filename = "V1_0__Initial_migration.sql";

            // when
            String description = MigrationUtils.extractDescriptionFromFilename(filename);

            // then
            Assertions.assertEquals("Initial.migration", description);
        }

        @Test
        void extractDescriptionFromFilename_shouldThrowIllegalArgumentException_whenFilenameIsInvalid() {
            // given
            String filename = "InvalidFilename.sql";

            // when & then
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> MigrationUtils.extractDescriptionFromFilename(filename));
        }
    }
}