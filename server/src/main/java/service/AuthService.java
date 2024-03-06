package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.SQLAuthDAO;
import models.AuthData;
import response.LogoutResponse;

import java.sql.SQLException;

public class AuthService {

    static AuthDAO authStoredDAO = new SQLAuthDAO();

    /**
     * @param authData containing authToken
     * @return full AuthData object, or null if authToken doesn't exist
     */
    public static AuthData getAuth(AuthData authData) {
        try {
            return authStoredDAO.getAuth(authData);
        } catch(DataAccessException | SQLException dae) {
            return null;
        }
    }

    /**
     * Called to delete an auth
     *
     * @param authData containing authToken
     * @return bool of success
     */
    public static LogoutResponse logout(AuthData authData) {
        try {
            authStoredDAO.deleteAuth(authData);
            return new LogoutResponse(200);
        } catch(DataAccessException dae) {
            return new LogoutResponse(401);
        }
    }

    public static boolean clearData() {
        try {
            authStoredDAO.clear();
            return true;
        } catch(DataAccessException dae) {
            return false;
        }
    }

    public static void setAuthDAO(AuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
