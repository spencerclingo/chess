package service;

import dataAccess.*;

public class ServiceInitializer {
    public static void initialize() throws DataAccessException {
        AuthDAO authDAO = new SQLAuthDAO();

        AuthService.setAuthDAO(authDAO);

        UserDAO userDAO = new SQLUserDAO();
        GameDAO gameDAO = new SQLGameDAO();

        GameService.setGameDAO(gameDAO);
        GameService.setAuthDAO(authDAO);
        UserService.setUserDAO(userDAO);
        UserService.setAuthDAO(authDAO);
        WebSocketService.setAuthStoredDAO(authDAO);
        WebSocketService.setGameStoredDAO(gameDAO);

        DatabaseManager.createDatabase();
        DatabaseManager.createAuthTable();
        DatabaseManager.createUserTable();
        DatabaseManager.createGameTable();
    }
}
