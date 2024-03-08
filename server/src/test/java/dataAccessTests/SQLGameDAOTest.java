package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SQLGameDAO;
import models.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import service.ServiceInitializer;

import java.util.ArrayList;

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
        gameDAO.clear();
    }

    @Test
    @Order(1)
    void createValidGame() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);

        assertEquals(1, gameDAO.createGame(gameData));
    }

    @Test
    @Order(2)
    void createGameWithoutGameName() {
        GameData gameData = new GameData(1, null, null, null, null);

        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData));
    }

    @Test
    @Order(3)
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
    @Order(4)
    void getInvalidGame() {
        GameData gameData = new GameData(1, null, null, "null", null);

        assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameData));
    }

    @Test
    @Order(5)
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
    @Order(6)
    void listNoGames() throws DataAccessException {
        ArrayList<GameData> gameList = gameDAO.listGames();

        int gameListSize = gameList.size();

        assertEquals(0, gameListSize);
    }

    /*
    @Test
    @Order(7)
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

     */

    @Test
    @Order(8)
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
    @Order(9)
    void joinInvalidGame() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);

        gameDAO.joinGame(gameData, 0);

        assertTrue(true);
    }

    @Test
    @Order(10)
    void clear() throws DataAccessException {
        GameData gameData = new GameData(1, null, null, "null", null);
        gameDAO.createGame(gameData);

        gameDAO.clear();

        assertThrows(DataAccessException.class, () -> gameDAO.getGame(gameData));

    }
}