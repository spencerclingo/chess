package dataAccess;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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

    public static void dropDatabase() throws DataAccessException {
        try {
            var statement = "DROP SCHEMA IF EXISTS `" + databaseName + "`;";

            try (var conn = DriverManager.getConnection(connectionUrl, user, password);
                 var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
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
    public static void createAuthTable() throws DataAccessException {
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
    public static void createUserTable() throws DataAccessException {
        try {
            var statement = """
                    CREATE TABLE IF NOT EXISTS `""" + databaseName + """
                    `.`users` (
                      `username` VARCHAR(100) NOT NULL,
                      `password` VARCHAR(255) NOT NULL,
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

    /**
     * Creates the gameTable in the Chess database
     *
     * @throws DataAccessException If table cannot be made
     */
    public static void createGameTable() throws DataAccessException {
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

    /**
     * Ideally executes a SQL string
     *
     * @param statement SQL query string
     * @param params    Array list of potential objects you can pass in
     *
     * @throws DataAccessException database is unable to be updated
     */
    public static boolean executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                preparedStatements(ps, params);
                int rowsAffected = ps.executeUpdate();

                return rowsAffected > 0;
            }
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException("unable to update database");
        }
    }

    /**
     * Helper function for SQL executions
     *
     * @param ps prepared statement
     * @param params array of parameters to be input into the statements
     * @throws SQLException if problems
     */
    private static void preparedStatements(PreparedStatement ps, Object[] params) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }
    }

    /**
     *
     * @param statement SQL query
     * @param params Array of objects that will be inserted into your query
     * @return ResultSet of what has been selected from the database
     * @throws DataAccessException if the database doesn't accept the SQL
     */
    public static ResultSet executeQuery(String statement, Object... params) throws DataAccessException {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement);


            // Set parameters if needed
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String) ps.setString(i + 1, (String) param);
                else if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);

            }

            // Execute the query
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
