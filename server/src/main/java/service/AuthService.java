package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import models.AuthData;

import javax.xml.crypto.Data;

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
    public static boolean logout(AuthData authData) {
        try {
            return authStoredDAO.deleteAuth(authData);
        } catch(DataAccessException dae) {
            return false;
        }
    }

    public static boolean confirmAuth(AuthData authData) {
        try {
            return authStoredDAO.confirmAuth(authData);
        } catch(DataAccessException dae) {
            return false;
        }
    }

    public static boolean clearData() {
        return authStoredDAO.clear();
    }

    public static void setAuthDAO(MemoryAuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
