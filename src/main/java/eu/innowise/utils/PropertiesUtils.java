package eu.innowise.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtils {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Configuration file 'application.properties' is not found in classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file.", e);
        }
    }

    private PropertiesUtils() {}

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
