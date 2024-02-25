package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import models.AuthData;
import models.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    static MemoryGameDAO gameDAO = new MemoryGameDAO();
    static MemoryAuthDAO authDAO = new MemoryAuthDAO();

    @BeforeAll
    static void setUp() {
        GameService.setGameDAO(gameDAO);
        GameService.setAuthDAO(authDAO);
    }

    @AfterEach
    void tearTown() {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void createNewGame() {
        GameData gameData = new GameData(-10, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        assertEquals(0, GameService.createGame(gameData, authData).gameID());
        assertEquals(1, GameService.createGame(gameData, authData).gameID());
        assertEquals(200, GameService.createGame(gameData, authData).HTTPCode());
    }

    @Test
    void createNewGameNoValidAuth() {
        GameData gameData = new GameData(-1, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");

        assertEquals(-1, GameService.createGame(gameData, authData).gameID());
        assertEquals(401, GameService.createGame(gameData, authData).HTTPCode());
    }

    @Test
    void getRealGame() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        gameDAO.createGame(gameData);

        assertNotNull(GameService.getGame(gameData));
    }

    @Test
    void getFakeGame() {
        GameData gameData = new GameData(0, "white", "black", "name", null);

        assertNull(GameService.getGame(gameData));
    }

    @Test
    void listGamesEmpty() {
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        assertEquals(new ArrayList<>(), GameService.listGames(authData).listOfGames());
        assertEquals(200, GameService.listGames(authData).HTTPCode());
    }

    @Test
    void listLotsOfGames() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);

        assertEquals(7, GameService.listGames(authData).listOfGames().size());
        assertEquals(200, GameService.listGames(authData).HTTPCode());
    }

    @Test
    void listGamesNoAuth() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");

        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);

        assertEquals(401, GameService.listGames(authData).HTTPCode());
    }

    @Test
    void successfulGameUpdate() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);

        assertEquals(1,GameService.updateGame(gameData, authData));
    }

    @Test
    void noGameAtIDToUpdate() {
        GameData gameData = new GameData(10, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);

        assertEquals(0,GameService.updateGame(gameData, authData));
    }

    @Test
    void noAuthorizationToUpdateGame() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");

        GameService.createGame(gameData, authData);

        assertEquals(-1,GameService.updateGame(gameData, authData));
    }

    @Test
    void clearGames() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");

        GameService.createGame(gameData, authData);

        assertTrue(GameService.clearGames());
    }
}