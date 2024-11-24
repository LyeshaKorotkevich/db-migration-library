package eu.innowise.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import eu.innowise.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static eu.innowise.utils.Constants.DEFAULT_REPORT_PATH;

/**
 * Class responsible for generating migration reports.
 * Reports are saved with a timestamp and success/failure status in the filename.
 */
@Slf4j
public class MigrationReportGenerator {

    /**
     * Generates a JSON report with the migration data.
     *
     * @param data The migration data to be included in the report.
     * @param success The status of the migration (success or failure).
     */
    public static void generateJsonReport(Object data, boolean success) {
        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
        Path outputPath = getReportDirectory(success);
        try {
            if (Files.notExists(outputPath.getParent())) {
                Files.createDirectories(outputPath.getParent());
                log.info("Created directories for the report at: {}", outputPath.getParent());
            }

            writer.writeValue(outputPath.toFile(), data);
            log.info("JSON report generated successfully at: {}", outputPath);
        } catch (IOException e) {
            log.error("Failed to generate JSON report.", e);
        }
    }

    private static Path getReportDirectory(boolean success) {
        String outputPath = PropertiesUtils.getProperty("report.output.path");
        if (outputPath != null && !outputPath.isBlank()) {
            return buildReportPath(outputPath, success);
        }

        return buildReportPath(DEFAULT_REPORT_PATH, success);
    }

    private static Path buildReportPath(String basePath, boolean success) {
        String status = success ? "success" : "failure";
        String timestamp = getCurrentTimestamp();
        String filename = String.format("migration_report_%s_%s.json", status, timestamp);
        return Path.of(basePath, filename);
    }

    private static String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return LocalDateTime.now().format(formatter);
    }
}
