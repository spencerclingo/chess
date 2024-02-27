package service;

import dataAccess.*;

public class ServiceInitializer {
    public static void initialize() {
        AuthDAO authDAO = new MemoryAuthDAO();

        AuthService.setAuthDAO(authDAO);

        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService.setGameDAO(gameDAO);
        GameService.setAuthDAO(authDAO);
        UserService.setUserDAO(userDAO);
        UserService.setAuthDAO(authDAO);
    }
}
