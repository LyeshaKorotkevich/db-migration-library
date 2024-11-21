package eu.innowise.utils;

public final class DatabaseUtils {

    private DatabaseUtils() {}

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
