package dataAccess;

import models.AuthData;

import java.sql.SQLException;

public interface AuthDAO {

    /**
     * @param authData containing username
     * @return full AuthData object
     */
    AuthData createAuth(AuthData authData) throws DataAccessException;

    /**
     * @param authData containing authToken
     * @return full AuthData object, or null if authToken doesn't exist
     */
    AuthData getAuth(AuthData authData) throws DataAccessException;

    /**
     * @param authData containing authToken
     */
    void deleteAuth(AuthData authData) throws DataAccessException;

    /**
     * Clears auth database
     */
    void clear() throws DataAccessException;
}
