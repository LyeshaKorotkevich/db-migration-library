package eu.innowise.migration;

import eu.innowise.utils.Constants;
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

    public List<Path> findMigrationFilesInResources() throws IOException, URISyntaxException {
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
            List<Path> migrationFiles = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(Constants.SQL_EXTENSION))
                    .toList();

            log.info("Found {} migration files in folder: {}", migrationFiles.size(), migrationsPath);
            return migrationFiles;
        } catch (IOException e) {
            log.error("Error accessing migration files in folder: {}", migrationsPath, e);
            throw e;
        }
    }

    public List<String> parseSqlFile(Path file) throws IOException {
        String content = Files.readString(file, StandardCharsets.UTF_8);
        return Stream.of(content.split(Constants.SEMICOLON))
                .map(String::trim)
                .toList();
    }
}