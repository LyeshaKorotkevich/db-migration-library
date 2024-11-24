package eu.innowise.utils;

import eu.innowise.exceptions.PropertiesLoadingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading and accessing properties from the 'application.properties' file.
 */
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

    /**
     * Retrieves the value of the specified property key.
     * If the property is not found, this method returns {@code null}.
     *
     * @param key The property key to look up.
     * @return The value of the property, or {@code null} if the key is not found.
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.warn("Property with key '{}' not found.", key);
        }
        return value;
    }
}
