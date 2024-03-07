package dataAccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE SCHEMA IF NOT EXISTS `" + databaseName + "`;";

            try (var conn = DriverManager.getConnection(connectionUrl, user, password);
                var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates the auth table if none exists
     *
     * @throws DataAccessException If the database doesn't allow it
     */
    static void createAuthTable() throws DataAccessException {
        try {
            var statement = """
                    CREATE TABLE IF NOT EXISTS `""" + databaseName + """
                    `.`auth` (
                    `authToken` VARCHAR(45) NOT NULL,
                    `username` VARCHAR(45) NOT NULL,
                    PRIMARY KEY (`authToken`)
                    );
                    """;
            try (var conn = DriverManager.getConnection(connectionUrl, user, password);
                var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates the user table if it does not exist already
     *
     * @throws DataAccessException if table could not be made properly
     */
    static void createUserTable() throws DataAccessException {
        try {
            var statement = """
                    CREATE TABLE IF NOT EXISTS `""" + databaseName + """
                    `.`user` (
                      `username` VARCHAR(45) NOT NULL,
                      `password` VARCHAR(45) NOT NULL,
                      `email` VARCHAR(90) NOT NULL,
                      PRIMARY KEY (`username`)
                    );
                    """;
            try (var conn = DriverManager.getConnection(connectionUrl, user, password);
                var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    static void createGameTable() throws DataAccessException {
        try {
            var statement = """
                    CREATE TABLE IF NOT EXISTS `""" + databaseName + """
                    `.`game` (
                      `gameID` INT NOT NULL,
                      `whiteUsername` VARCHAR(45),
                      `blackUsername` VARCHAR(45),
                      `gameName` VARCHAR(45) NOT NULL,
                      `game` TEXT,
                      PRIMARY KEY (`gameID`)
                    );
                    """;
            try (var conn = DriverManager.getConnection(connectionUrl, user, password);
                 var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
