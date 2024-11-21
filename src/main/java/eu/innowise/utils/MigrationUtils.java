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

@Slf4j
public final class MigrationUtils {

    private static final int GROUP_VERSION_NUMBER = 1;
    private static final int GROUP_DESCRIPTION_NUMBER = 2;

    private MigrationUtils() {
    }

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

    public static String extractVersionFromFilename(String filename) {
        Pattern pattern = Pattern.compile(Constants.MIGRATION_FILE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(GROUP_VERSION_NUMBER).
                    replace("_", ".");
        }
        log.error("Invalid migration filename: {}", filename);
        throw new IllegalArgumentException("Invalid migration filename: " + filename);
    }

    public static String extractDescriptionFromFilename(String filename) {
        Pattern pattern = Pattern.compile(Constants.MIGRATION_FILE_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            return matcher.group(GROUP_DESCRIPTION_NUMBER);
        }
        log.error("Invalid migration filename: {}", filename);
        throw new IllegalArgumentException("Invalid migration filename: " + filename);
    }
}
