package mhd.sosrota.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Class DatabaseConnection
 */
public class DatabaseConnection {

    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(EnvConfig.get("DATABASE_URL"));
        }
        return connection;
    }
}
