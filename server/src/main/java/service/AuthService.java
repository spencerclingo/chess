package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import models.AuthData;
import response.LogoutResponse;

public class AuthService {

    static AuthDAO authStoredDAO;

    /**
     * @param authData containing authToken
     * @return full AuthData object, or null if authToken doesn't exist
     */
    public static AuthData getAuth(AuthData authData) {
        try {
            return authStoredDAO.getAuth(authData);
        } catch(DataAccessException dae) {
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
        authStoredDAO.clear();
        return true;
    }

    public static void setAuthDAO(AuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
