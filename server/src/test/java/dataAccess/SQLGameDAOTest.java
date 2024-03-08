package dataAccess;

import models.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ServiceInitializer;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    SQLGameDAO gameDAO = new SQLGameDAO();

    @BeforeAll
    static void setup() throws DataAccessException {
        ServiceInitializer.initialize();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    void createValidGame() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);

        assertEquals(1, gameDAO.createGame(gameData));
    }

    @Test
    void createGameWithoutGameName() {
        GameData gameData = new GameData(1, null, null, null, null);

        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData));
    }

    @Test
    void getValidGame() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);

        gameDAO.createGame(gameData);

        try {
            assertEquals(gameData, gameDAO.getGame(gameData));
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    void getInvalidGame() {
        GameData gameData = new GameData(1, null, null, "null", null);

        assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameData));
    }

    @Test
    void listGames() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);
        gameDAO.createGame(gameData);
        gameDAO.createGame(gameData);
        gameDAO.createGame(gameData);
        gameDAO.createGame(gameData);
        gameDAO.createGame(gameData);

        assertEquals(5, gameDAO.listGames().size());
    }

    @Test
    void listNoGames() throws DataAccessException {
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    void joinValidGameAsWhite() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);
        gameDAO.createGame(gameData);

        GameData gameData1 = new GameData(1, "white", null, "null", null);

        gameDAO.joinGame(gameData1, 0);

        try {
            assertEquals("white", gameDAO.getGame(gameData1).whiteUsername());
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    void joinValidGameAsBlack() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);
        gameDAO.createGame(gameData);

        GameData gameData1 = new GameData(1, null, "black", "null", null);

        gameDAO.joinGame(gameData1, 1);

        try {
            assertEquals("black", gameDAO.getGame(gameData1).blackUsername());
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    void joinInvalidGame() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);

        gameDAO.joinGame(gameData, 0);

        assertTrue(true);
    }

    @Test
    void clear() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);
        gameDAO.createGame(gameData);

        gameDAO.clear();

        assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameData));

    }
}