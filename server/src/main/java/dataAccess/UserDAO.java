package dataAccess;

import models.UserData;

public interface UserDAO {

    /**
     * Clears user database
     */
    void clear() throws DataAccessException;

    /**
     * @param userData containing username, password, email
     */
    void createUser(UserData userData) throws DataAccessException;

    /**
     * returns if the login information is valid
     *
     * @param userData containing a username and password
     * @return true if user/pass match, false if they don't
     */
    boolean login(UserData userData) throws DataAccessException;

    /**
     * @param userData contains username
     * @return full UserData object
     */
    UserData getUser(UserData userData) throws DataAccessException;
}
