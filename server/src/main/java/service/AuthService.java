package service;

import dataAccess.MemoryAuthDAO;
import models.AuthData;

public class AuthService {

    static MemoryAuthDAO authStoredDAO;

    /**
     * @param authData containing username
     * @return full AuthData object
     */
    public static AuthData createAuth(AuthData authData) {
        return authStoredDAO.createAuth(authData);
    }

    /**
     * @param authData containing authToken
     * @return full AuthData object, or null if authToken doesn't exist
     */
    public static AuthData getAuth(AuthData authData) {
        return authStoredDAO.getAuth(authData);
    }

    /**
     * Called to delete an auth
     *
     * @param authData containing authToken
     * @return bool of success
     */
    public static boolean logout(AuthData authData) {
        return authStoredDAO.deleteAuth(authData);
    }

    public static boolean clearData() {
        return authStoredDAO.clear();
    }

    public static void setAuthDAO(MemoryAuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
