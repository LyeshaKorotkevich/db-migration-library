package eu.innowise.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

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

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Error getting connection from the pool", e);
            throw new RuntimeException("Error getting connection from the pool", e);
        }
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("DataSource closed successfully");
        }
    }
}
