package eu.innowise.utils;

/**
 * Utility class for database-related operations.
 * This class contains methods to determine the database type based on configuration properties.
 * It uses the database driver class name or URL to infer the database type.
 */
public final class DatabaseUtils {

    private DatabaseUtils() {}

    /**
     * Retrieves the database type based on the configuration properties.
     * It checks the driver class name or JDBC URL to determine the type of the database.
     * <p>
     * Supported databases: PostgreSQL, MySQL, and H2.
     * If an unsupported database is detected, an exception is thrown.
     *
     * @return The database type as a String ("postgresql", "mysql", or "h2").
     * @throws IllegalArgumentException If the database type is unsupported.
     */
    public static String getDatabaseType() {
        String driverClassName = PropertiesUtils.getProperty("db.driver-class-name");
        String url = PropertiesUtils.getProperty("db.url");

        if (driverClassName.contains("postgresql") || url.startsWith("jdbc:postgresql")) {
            return "postgresql";
        } else if (driverClassName.contains("mysql") || url.startsWith("jdbc:mysql")) {
            return "mysql";
        } else if (driverClassName.contains("h2") || url.startsWith("jdbc:h2")) {
            return "h2";
        } else {
            throw new IllegalArgumentException("Unsupported database type in configuration.");
        }
    }
}
