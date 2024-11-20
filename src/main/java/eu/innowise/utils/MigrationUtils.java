package eu.innowise.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public final class MigrationUtils {

    private MigrationUtils() {
    }

    public static int calculateChecksum(Path file) {
        try (InputStream inputStream = Files.newInputStream(file)) {
            String sha256Hex = DigestUtils.sha256Hex(inputStream);
            log.debug("Calculated checksum for file {}: {}", file.getFileName(), sha256Hex);
            return sha256Hex.hashCode();
        } catch (IOException e) {
            log.error("Failed to calculate checksum for file: {}", file, e);
            throw new RuntimeException("Failed to calculate checksum for file: " + file, e);
        }
    }
}
