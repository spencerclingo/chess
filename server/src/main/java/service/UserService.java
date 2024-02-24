package service;

import dataAccess.MemoryUserDAO;

public class UserService {
    static MemoryUserDAO userStoredDAO;

    public static boolean clearData() {
        if (userStoredDAO.clear()) {
            return true;
        }
        return false;
        //TODO: This might not be returning a boolean later on, might do some weird objects
    }


    public static void setUserDAO(MemoryUserDAO userDAO) {
        userStoredDAO=userDAO;
    }
}
