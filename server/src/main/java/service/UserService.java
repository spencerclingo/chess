package service;

import com.mysql.cj.log.Log;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import models.AuthData;
import models.UserData;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;

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
     * @return AuthData for the authToken if information matches, null if it does not
     */
    public static LoginResponse login(UserData userData) {
        try {
            userStoredDAO.getUser(userData);
            if (userStoredDAO.login(userData)) {
                return new LoginResponse(authStoredDAO.createAuth(new AuthData(null, userData.username())), 200);
            }
            return new LoginResponse(null, 401);
        } catch(DataAccessException dae) {
            return new LoginResponse(null, 401);
        }
    }

    /**
     * @param userData contains username, password, email
     * @return bool of success to create
     */
    public static RegisterResponse createUser(UserData userData) {
        if (userData.password() == null) {
            return new RegisterResponse(null, 400);
        }
        try {
            userStoredDAO.getUser(userData);
            return new RegisterResponse(null, 403);
        } catch(DataAccessException dae) {
            userStoredDAO.createUser(userData);
            return new RegisterResponse(authStoredDAO.createAuth(new AuthData(null, userData.username())), 200);
        }
    }

    /**
     * @return bool of success
     */
    public static boolean clearData() {
        return userStoredDAO.clear();
    }

    /**
     * Logging out involves user, call UserService
     *
     * @param authData contains authToken
     * @return success of logging out
     */
    public static LogoutResponse logout(AuthData authData) {
        return AuthService.logout(authData);
    }


    public static void setUserDAO(MemoryUserDAO userDAO) {
        userStoredDAO=userDAO;
    }

    public static void setAuthDAO(MemoryAuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
