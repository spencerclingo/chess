package dataAccess;

import models.AuthData;

public interface AuthDAO {

    /**
     * @param authData containing username
     * @return full AuthData object
     */
    AuthData createAuth(AuthData authData);

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
    void clear();
}
