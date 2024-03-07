package dataAccess;

import models.AuthData;

import java.util.UUID;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{
    /**
     * Ideally executes a SQL string
     *
     * @param statement SQL query string
     * @param params    Array list of potential objects you can pass in
     *
     * @throws DataAccessException database is unable to be updated
     */
    private boolean executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    System.out.println("param[" + i + "] = "+param);
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                int rowsAffected = ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

                return rowsAffected > 0;
            }
        } catch(SQLException | DataAccessException e) {
            System.out.println("Error thrown when executing update: " + e.getMessage());
            throw new DataAccessException("unable to update database: %s, %s");
        }
    }

    /**
     *
     * @param statement SQL query
     * @param params Array of objects that will be inserted into your query
     * @return ResultSet of what has been selected from the database
     * @throws DataAccessException if the database doesn't accept the SQL
     */
    private ResultSet executeQuery(String statement, Object... params) throws DataAccessException {
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

    /**
     * @param authData containing username
     *
     * @return full AuthData object
     */
    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException {
        String username = authData.username();
        String authToken = UUID.randomUUID().toString();
        AuthData authData1 = new AuthData(authToken, username);

        System.out.println("creating authToken: " + authToken);

        DatabaseManager.createAuthTable();

        String statement = "INSERT INTO `auth` (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);
        return authData1;
    }

    /**
     * @param authData containing authToken
     *
     * @return full AuthData object, or null if authToken doesn't exist
     */
    @Override
    public AuthData getAuth(AuthData authData) throws DataAccessException {
        DatabaseManager.createAuthTable();
        String authToken = authData.authToken();
        String username = null;

        String statement = "SELECT * FROM `auth` WHERE `authToken` = ?;";
        try(ResultSet resultSet = executeQuery(statement, authToken)) {
            while (resultSet.next()) {
                username = resultSet.getString("username");
            }
            if (username == null) {
                throw new DataAccessException("No username matches authToken");
            }
            return new AuthData(authToken, username);
        } catch(SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @param authData containing authToken
     */
    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        DatabaseManager.createAuthTable();
        String authToken = authData.authToken();

        System.out.println("Deleting auth: " + authToken);

        String statement = "DELETE FROM `chess`.`auth` WHERE `authToken` = ?";

        boolean successfulDelete = executeUpdate(statement, authToken);
        if (!successfulDelete) {
            throw new DataAccessException("Data could not be deleted");
        }
    }

    /**
     * Clears auth database
     */
    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.createAuthTable();
        String statement = "DELETE FROM `chess`.`auth`;";

        executeUpdate(statement);
    }
}
