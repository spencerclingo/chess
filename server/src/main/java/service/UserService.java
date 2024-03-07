package service;

import dataAccess.*;
import models.AuthData;
import models.UserData;
import response.LoginResponse;
import response.RegisterResponse;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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
            System.out.println("Username and password don't match");
            return new LoginResponse(null, 401);
        } catch(DataAccessException dae) {
            System.out.println("Exception thrown while logging in");
            return new LoginResponse(null, 401);
        }
    }

    /**
     * @param userData contains username, password, email
     * @return bool of success to create
     * @throws DataAccessException when user creation fails in the database
     */
    public static RegisterResponse createUser(UserData userData) throws DataAccessException {
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
     * Clears user database
     */
    public static boolean clearData() {
        try {
            userStoredDAO.clear();
        } catch(DataAccessException dae) {
            return false;
        }
        return true;
    }

    public static void setUserDAO(UserDAO userDAO) {
        userStoredDAO=userDAO;
    }

    public static void setAuthDAO(AuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
