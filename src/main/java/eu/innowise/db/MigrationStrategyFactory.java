package eu.innowise.db;

import eu.innowise.db.impl.H2MigrationStrategy;
import eu.innowise.db.impl.MySQLMigrationStrategy;
import eu.innowise.db.impl.PostgresMigrationStrategy;

public class MigrationStrategyFactory {

    private MigrationStrategyFactory() {}

    public static MigrationStrategy getMigrationStrategy(String dbType) {
        return switch (dbType.toLowerCase()) {
            case "postgresql" -> new PostgresMigrationStrategy();
            case "mysql" -> new MySQLMigrationStrategy();
            case "h2" -> new H2MigrationStrategy();
            default -> throw new IllegalArgumentException("Unsupported database type: " + dbType);
        };
    }
}
