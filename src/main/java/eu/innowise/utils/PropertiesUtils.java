package eu.innowise.utils;

import eu.innowise.exceptions.PropertiesLoadingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public final class PropertiesUtils {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                log.error("Configuration file 'application.properties' is not found in the classpath.");
                throw new IOException("Configuration file 'application.properties' is not found.");
            }
            properties.load(input);
            log.info("Successfully loaded 'application.properties'.");
        } catch (IOException e) {
            log.error("Failed to load 'application.properties'.", e);
            throw new PropertiesLoadingException("Failed to load properties file.", e);
        }
    }

    private PropertiesUtils() {}

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
