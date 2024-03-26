package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import models.AuthData;
import models.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import response.JoinGameRequest;
import service.GameService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();

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

        assertEquals(1, GameService.createGame(gameData, authData).gameID());
        assertEquals(2, GameService.createGame(gameData, authData).gameID());
        assertEquals(200, GameService.createGame(gameData, authData).httpCode());
    }

    @Test
    void createNewGameNoValidAuth() {
        GameData gameData = new GameData(-1, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");

        assertEquals(-1, GameService.createGame(gameData, authData).gameID());
        assertEquals(401, GameService.createGame(gameData, authData).httpCode());
    }

    @Test
    void listGamesEmpty() {
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        assertEquals(new ArrayList<>(), GameService.listGames(authData).listOfGames());
        assertEquals(200, GameService.listGames(authData).httpCode());
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
        assertEquals(200, GameService.listGames(authData).httpCode());
    }

    @Test
    void listGamesNoAuth() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");

        GameService.createGame(gameData, authData);
        GameService.createGame(gameData, authData);

        assertEquals(401, GameService.listGames(authData).httpCode());
    }

    @Test
    void joinValidGame() throws DataAccessException {
        GameData gameData = new GameData(1, "white", null, "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);

        assertEquals(200, GameService.joinGame(new JoinGameRequest("black", 1), authData).httpCode());
    }

    @Test
    void joinInvalidGameID() throws DataAccessException {
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        assertEquals(400, GameService.joinGame(new JoinGameRequest("black", 1), authData).httpCode());
    }

    @Test
    void joinColorAlreadyTaken() throws DataAccessException {
        GameData gameData = new GameData(1, "white", "taken", "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);

        assertEquals(403, GameService.joinGame(new JoinGameRequest("black", 1), authData).httpCode());
    }

    @Test
    void watchGameValid() throws DataAccessException {
        GameData gameData = new GameData(1, "white", null, "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);

        assertEquals(200, GameService.joinGame(new JoinGameRequest("", 1), authData).httpCode());
    }

    @Test
    void watchGameInvalidGameID() throws DataAccessException {
        GameData gameData = new GameData(1, "white", null, "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);

        assertEquals(400, GameService.joinGame(new JoinGameRequest("", 10), authData).httpCode());
    }

    @Test
    void watchGameMultipleTimes() throws DataAccessException {
        GameData gameData = new GameData(1, "white", null, "name", null);
        AuthData authData = new AuthData("12345", "username");
        authData = authDAO.createAuth(authData);

        GameService.createGame(gameData, authData);

        assertEquals(200, GameService.joinGame(new JoinGameRequest("", 1), authData).httpCode());
        assertEquals(200, GameService.joinGame(new JoinGameRequest("", 1), authData).httpCode());
        assertEquals(200, GameService.joinGame(new JoinGameRequest("", 1), authData).httpCode());
    }

    @Test
    void clearGames() {
        GameData gameData = new GameData(0, "white", "black", "name", null);
        AuthData authData = new AuthData("12345", "username");

        GameService.createGame(gameData, authData);

        assertTrue(GameService.clearGames());
    }
}