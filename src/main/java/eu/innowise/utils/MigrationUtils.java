package eu.innowise.utils;

import eu.innowise.exceptions.ChecksumCalculationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for migration-related operations, such as calculating checksums and extracting version and description from filenames.
 */
@Slf4j
public final class MigrationUtils {

    private static final int GROUP_VERSION_NUMBER = 1;
    private static final int GROUP_DESCRIPTION_NUMBER = 2;

    private MigrationUtils() {
    }

    /**
     * Calculates the checksum (MD5 hash) of a file and returns it as an integer.
     * This method reads the file and computes the MD5 hash, which is then converted to a hash code.
     *
     * @param file The file for which to calculate the checksum.
     * @return The checksum of the file as an integer.
     * @throws ChecksumCalculationException If an error occurs while calculating the checksum.
     */
    public static int calculateChecksum(Path file) {
        try (InputStream inputStream = Files.newInputStream(file)) {
            String md5Hex = DigestUtils.md5Hex(inputStream);
            log.debug("Calculated checksum for file {}: {}", file.getFileName(), md5Hex);
            return md5Hex.hashCode();
        } catch (IOException e) {
            log.error("Failed to calculate checksum for file: {}", file, e);
            throw new ChecksumCalculationException("Failed to calculate checksum for file: " + file, e);
        }
    }

    /**
     * Extracts the version from a migration filename based on a predefined naming pattern.
     * The version is extracted from the filename using a regular expression.
     *
     * @param filename The migration filename to extract the version from.
     * @return The extracted version as a string.
     * @throws IllegalArgumentException If the filename does not match the expected pattern.
     */
    public static String extractVersionFromFilename(String filename) {
        return extractFromFilename(filename, GROUP_VERSION_NUMBER);
    }

    /**
     * Extracts the description from a migration filename based on a predefined naming pattern.
     * The description is extracted from the filename using a regular expression.
     *
     * @param filename The migration filename to extract the description from.
     * @return The extracted description as a string.
     * @throws IllegalArgumentException If the filename does not match the expected pattern.
     */
    public static String extractDescriptionFromFilename(String filename) {
        return extractFromFilename(filename, GROUP_DESCRIPTION_NUMBER);
    }

    private static String extractFromFilename(String filename, int groupNumber) {
        Pattern pattern = Pattern.compile(Constants.MIGRATION_FILE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(groupNumber).replace("_", ".");
        }
        log.error("Invalid migration filename: {}", filename);
        throw new IllegalArgumentException("Invalid migration filename: " + filename);
    }
}