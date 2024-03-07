package service;

import dataAccess.*;

public class ServiceInitializer {
    public static void initialize() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();

        AuthService.setAuthDAO(authDAO);

        UserDAO userDAO = new SQLUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService.setGameDAO(gameDAO);
        GameService.setAuthDAO(authDAO);
        UserService.setUserDAO(userDAO);
        UserService.setAuthDAO(authDAO);

        DatabaseManager.createDatabase();
        DatabaseManager.createAuthTable();
        DatabaseManager.createUserTable();
        DatabaseManager.createGameTable();
    }
}
