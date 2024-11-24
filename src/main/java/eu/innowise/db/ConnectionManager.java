package eu.innowise.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.innowise.exceptions.DbConnectionException;
import eu.innowise.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database connections using HikariCP connection pooling.
 * It provides methods to get a connection and close the data source.
 */
@Slf4j
public final class ConnectionManager {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(PropertiesUtils.getProperty("db.url"));
        config.setUsername(PropertiesUtils.getProperty("db.username"));
        config.setPassword(PropertiesUtils.getProperty("db.password"));
        config.setDriverClassName(PropertiesUtils.getProperty("db.driver-class-name"));
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    private ConnectionManager() {
    }

    /**
     * Gets a connection from the Hikari connection pool.
     *
     * @return A connection to the database.
     * @throws DbConnectionException If an error occurs while obtaining the connection.
     */
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Error getting connection from the pool", e);
            throw new DbConnectionException("Error getting connection from the pool", e);
        }
    }

    /**
     * Closes the data source and releases all resources.
     * Should be called when the application is shutting down.
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("DataSource closed successfully");
        }
    }
}
