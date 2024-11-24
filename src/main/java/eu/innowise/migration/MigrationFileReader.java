package eu.innowise.migration;

import eu.innowise.exceptions.MigrationFileReadException;
import eu.innowise.model.Migration;
import eu.innowise.utils.Constants;
import eu.innowise.utils.MigrationUtils;
import eu.innowise.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class MigrationFileReader {

    public List<Migration> findMigrationFilesInResources() throws IOException, URISyntaxException {
        String migrationsPath = PropertiesUtils.getProperty("migration.folder");
        if (migrationsPath == null) {
            migrationsPath = Constants.DEFAULT_MIGRATIONS_PATH;
        }

        URL resourceUrl = ClassLoader.getSystemResource(migrationsPath);
        if (resourceUrl == null) {
            log.error("Migration folder not found: {}", migrationsPath);
            throw new IllegalArgumentException("Migration folder not found: " + migrationsPath);
        }

        try (Stream<Path> paths = Files.walk(Paths.get(resourceUrl.toURI()))) {
            List<Migration> migrations = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(Constants.SQL_EXTENSION))
                    .map(this::toMigration)
                    .toList();

            log.info("Found {} migration files in folder: {}", migrations.size(), migrationsPath);
            return migrations;
        } catch (IOException e) {
            log.error("Error accessing migration files in folder: {}", migrationsPath, e);
            throw e;
        }
    }

    private Migration toMigration(Path path) {
        String filename = path.getFileName().toString();
        String version = MigrationUtils.extractVersionFromFilename(filename);
        String description = MigrationUtils.extractDescriptionFromFilename(filename);
        int checksum = MigrationUtils.calculateChecksum(path);

        try {
            List<String> sqlStatements = parseSqlFile(path);
            return new Migration(version, description, checksum, sqlStatements);
        } catch (IOException e) {
            log.error("Error reading SQL from migration file: {}", filename, e);
            throw new MigrationFileReadException("Error reading migration file: " + filename, e);
        }
    }

    public List<String> parseSqlFile(Path file) throws IOException {
        String content = Files.readString(file, StandardCharsets.UTF_8);
        return Stream.of(content.split(Constants.SEMICOLON))
                .map(String::trim)
                .toList();
    }
}