package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import models.GameData;
import models.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    ClearService clearService = new ClearService();
    MemoryAuthDAO authDAO = new MemoryAuthDAO();
    MemoryUserDAO userDAO = new MemoryUserDAO();
    MemoryGameDAO gameDAO = new MemoryGameDAO();

    @BeforeEach
    void setUp() {
        clearService.setAuthDAO(authDAO);
        clearService.setUserDAO(userDAO);
        clearService.setGameDAO(gameDAO);
    }

    @Test
    void clearEmptyData() {
        assertTrue(clearService.clearData());
    }

    @Test
    void clearAddedData() {
        authDAO.createAuth("username");
        userDAO.createUser(new UserData("username", "password", "email@email"));
        gameDAO.createGame(new GameData(123, null, null, null, null));

        assertTrue(clearService.clearData());
    }
}