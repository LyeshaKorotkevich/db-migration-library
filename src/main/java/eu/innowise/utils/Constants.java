package eu.innowise.utils;

public final class Constants {

    private Constants() {
    }

    public static final String SEMICOLON = ";";
    public static final String SQL_EXTENSION = ".sql";

    public static final String MIGRATION_FILE_PATTERN = "^V(\\d+(?:[._]\\d+)?)__(.*)\\.sql$";
    public static final String DEFAULT_MIGRATIONS_PATH = "migrations";

    public static final String SCHEMA_HISTORY_TABLE = "schema_history";

    // queries

    // Postgres
    public static final String CREATE_SCHEMA_TABLE_PG = """
            CREATE TABLE IF NOT EXISTS schema_history (
                installed_rank SERIAL PRIMARY KEY,
                version VARCHAR(15) NOT NULL,
                description VARCHAR(200) NOT NULL,
                checksum INT,
                installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

    // MySql
    public static final String CREATE_SCHEMA_TABLE_MYSQL = """
            CREATE TABLE IF NOT EXISTS schema_history (
                installed_rank INT AUTO_INCREMENT PRIMARY KEY,
                version VARCHAR(15) NOT NULL,
                description VARCHAR(200) NOT NULL,
                checksum INT,
                installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

    public static final String SELECT_VERSIONS_AND_CHECKSUMS_FROM_SCHEMA_HISTORY = "SELECT version, checksum FROM " + SCHEMA_HISTORY_TABLE;
    public static final String INSERT_SCHEMA_HISTORY = "INSERT INTO " + Constants.SCHEMA_HISTORY_TABLE +
            " (version, description, checksum) VALUES (?, ?, ?)";
    public static final String SELECT_SCHEMA_HISTORY_FOR_UPDATE = "SELECT * FROM " + Constants.SCHEMA_HISTORY_TABLE + " FOR UPDATE";
}
