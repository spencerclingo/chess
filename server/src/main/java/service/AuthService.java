package service;

import dataAccess.MemoryAuthDAO;
import models.AuthData;

public class AuthService {

    static MemoryAuthDAO authStoredDAO;

    public static boolean logout(AuthData authData) {
        return authStoredDAO.deleteAuth(authData);
    }

    public static boolean clearData() {
        if (authStoredDAO.clear()) {
            return true;
        }
        return false;
        //TODO: This might not be returning a boolean later on, might do some weird objects
    }

    public static void setAuthDAO(MemoryAuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
