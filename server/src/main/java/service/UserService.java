package service;

import dataAccess.MemoryUserDAO;
import models.AuthData;
import models.UserData;
import org.springframework.security.core.userdetails.User;

public class UserService {
    static MemoryUserDAO userStoredDAO;

    /**
     * @param userData contains username
     * @return bool if the user already exists
     */
    public static boolean getUser(UserData userData) {
        UserData fullUserData = userStoredDAO.getUser(userData);

        return (fullUserData != null);
    }

    /**
     * @param userData contains username, password, email
     * @return bool of success to create
     */
    public static boolean createUser(UserData userData) {
        return userStoredDAO.createUser(userData);
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
}
