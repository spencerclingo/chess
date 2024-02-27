package service;

import dataAccess.*;
import models.AuthData;
import models.UserData;
import response.LoginResponse;
import response.RegisterResponse;

public class UserService {
    static UserDAO userStoredDAO;
    static AuthDAO authStoredDAO = AuthService.authStoredDAO;

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

    public static void setUserDAO(UserDAO userDAO) {
        userStoredDAO=userDAO;
    }

    public static void setAuthDAO(AuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
