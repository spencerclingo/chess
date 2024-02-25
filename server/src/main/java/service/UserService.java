package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import models.AuthData;
import models.UserData;
import org.springframework.security.core.userdetails.User;

public class UserService {
    static MemoryUserDAO userStoredDAO;
    static MemoryAuthDAO authStoredDAO = AuthService.authStoredDAO;

    /**
     * @param userData contains username
     * @return bool if the user already exists
     */
    public static boolean getUser(UserData userData) {
        try {
            return (userStoredDAO.getUser(userData) != null);
        } catch(DataAccessException dae) {
            return false;
        }
    }

    /**
     * @param userData contains username and password
     * @return true if information matches, false if it does not
     */
    public static boolean login(UserData userData) {
        try {
            userStoredDAO.getUser(userData);
            return userStoredDAO.login(userData);
        } catch(DataAccessException dae) {
            return false;
        }
    }

    /**
     * @param userData contains username, password, email
     * @return bool of success to create
     */
    public static AuthData createUser(UserData userData) {
        try {
            userStoredDAO.getUser(userData);
            return null;
        } catch(DataAccessException dae) {
            userStoredDAO.createUser(userData);
            return authStoredDAO.createAuth(new AuthData(null, userData.username()));
        }
    }

    /**
     * @return bool of success
     */
    public static boolean clearData() {
        return userStoredDAO.clear();
    }


    public static void setUserDAO(MemoryUserDAO userDAO) {
        userStoredDAO=userDAO;
    }

    public static void setAuthDAO(MemoryAuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
