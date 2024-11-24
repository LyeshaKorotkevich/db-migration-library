package eu.innowise.db;

import eu.innowise.db.impl.H2MigrationStrategy;
import eu.innowise.db.impl.MySQLMigrationStrategy;
import eu.innowise.db.impl.PostgresMigrationStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating migration strategies based on the database type.
 */
public class MigrationStrategyFactory {

    private static final Map<String, MigrationStrategy> migrationStrategies = new HashMap<>();

    static {
        migrationStrategies.put("postgresql", new PostgresMigrationStrategy());
        migrationStrategies.put("mysql", new MySQLMigrationStrategy());
        migrationStrategies.put("h2", new H2MigrationStrategy());
    }

    private MigrationStrategyFactory() {}

    /**
     * Returns the appropriate migration strategy based on the provided database type.
     *
     * @param dbType The type of the database (e.g., "postgresql", "mysql", "h2").
     * @return The corresponding migration strategy.
     * @throws IllegalArgumentException If the provided database type is unsupported.
     */
    public static MigrationStrategy getMigrationStrategy(String dbType) {
        MigrationStrategy strategy = migrationStrategies.get(dbType.toLowerCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }

        return strategy;
    }
}
