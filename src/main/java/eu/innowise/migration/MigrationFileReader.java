package eu.innowise.migration;

import eu.innowise.utils.PropertiesUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class MigrationFileReader {

    private static final String DEFAULT_MIGRATIONS_PATH = "migrations";

    public List<Path> findMigrationFilesInResources() throws IOException, URISyntaxException {
        String migrationsPath = PropertiesUtils.getProperty("migration.folder");
        if (migrationsPath == null) {
            migrationsPath = DEFAULT_MIGRATIONS_PATH;
        }

        URL resourceUrl = ClassLoader.getSystemResource(migrationsPath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Migration folder not found: " + migrationsPath);
        }

        try (Stream<Path> paths = Files.walk(Paths.get(resourceUrl.toURI()))) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".sql"))
                    .toList();
        }
    }

    public List<String> parseSqlFile(Path file) throws IOException {
        String content = Files.readString(file, StandardCharsets.UTF_8);
        return Stream.of(content.split(";"))
                .map(String::trim)
                .toList();
    }
}