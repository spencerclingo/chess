package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;

public class ClearService {
    MemoryAuthDAO authDAO;
    MemoryGameDAO gameDAO;
    MemoryUserDAO userDAO;

    public boolean clearData() {
        if (authDAO.clear() && gameDAO.clear() && userDAO.clear()) {
            return true;
        }
        return false;
        //TODO: This might not be returning a boolean later on, might do some weird objects
    }

    public void setAuthDAO(MemoryAuthDAO authDAO) {
        this.authDAO=authDAO;
    }

    public void setGameDAO(MemoryGameDAO gameDAO) {
        this.gameDAO=gameDAO;
    }

    public void setUserDAO(MemoryUserDAO userDAO) {
        this.userDAO=userDAO;
    }
}
