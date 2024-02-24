package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
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
    void clearData() {
        assertTrue(clearService.clearData());
    }
}