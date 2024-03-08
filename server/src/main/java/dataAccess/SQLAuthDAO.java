package dataAccess;

import models.AuthData;

import java.util.UUID;
import java.sql.*;

public class SQLAuthDAO implements AuthDAO{

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

        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(statement, authToken, username);
        return authData1;
    }

    /**
     * @param authData containing authToken
     *
     * @return full AuthData object, or null if authToken doesn't exist
     */
    @Override
    public AuthData getAuth(AuthData authData) throws DataAccessException {
        String authToken = authData.authToken();
        String username = null;

        System.out.println("This is called");

        String statement = "SELECT * FROM `auth` WHERE `authToken` = ?;";
        try(ResultSet resultSet = DatabaseManager.executeQuery(statement, authToken)) {
            while (resultSet.next()) {
                username = resultSet.getString("username");
            }
            if (username == null) {
                throw new DataAccessException("No username matches authToken");
            }
            return new AuthData(authToken, username);
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @param authData containing authToken
     */
    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        String authToken = authData.authToken();

        String statement = "DELETE FROM `chess`.`auth` WHERE `authToken` = ?";

        boolean successfulDelete = DatabaseManager.executeUpdate(statement, authToken);
        if (!successfulDelete) {
            throw new DataAccessException("Data could not be deleted");
        }
    }

    /**
     * Clears auth database
     */
    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM `chess`.`auth`;";

        DatabaseManager.executeUpdate(statement);
    }
}
