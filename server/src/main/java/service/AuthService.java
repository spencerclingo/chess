package service;

import dataAccess.MemoryAuthDAO;

public class AuthService {

    static MemoryAuthDAO authStoredDAO;

    public static boolean logout(String authToken) {
        return authStoredDAO.deleteAuth(authToken);
    }

    public static void setAuthDAO(MemoryAuthDAO authDAO) {
        authStoredDAO=authDAO;
    }
}
