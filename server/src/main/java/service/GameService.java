package service;

import dataAccess.*;
import models.AuthData;
import models.GameData;
import response.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class GameService {
    static GameDAO gameStoredDAO = new MemoryGameDAO();
    static AuthDAO authStoredDAO = AuthService.authStoredDAO;

    /**
     * @param gameData contains gameName. Can contain chessGame
     * @return CreateGameResponse with new games ID or -1 if authToken is invalid
     */
    public static CreateGameResponse createGame(GameData gameData, AuthData authData) {
        try {
            authStoredDAO.getAuth(authData);
            return new CreateGameResponse(gameStoredDAO.createGame(gameData), 200);
        } catch(DataAccessException dae) {
            return new CreateGameResponse(-1, 401);
        }
    }

    /**
     * @param gameData Must contain gameID
     * @return full GameData object of matching game or null
     */
    public static GameData getGame(GameData gameData) {
        try {
            return gameStoredDAO.getGame(gameData);
        } catch(DataAccessException dae) {
            return null;
        }
    }

    /**
     * @param authData They must be a valid use to get the game info
     * @return List of games, or null if they are invalid users
     */
    public static ListGamesResponse listGames(AuthData authData) {
        try {
            authStoredDAO.getAuth(authData);
            return new ListGamesResponse(gameStoredDAO.listGames(), 200);
        } catch(DataAccessException dae) {
            return new ListGamesResponse(null, 401);
        }
    }

    /**
     * @param gameData contains at least gameID plus current status of game
     * @param authData authToken
     * @return 1 if game was updated, 0 if game didn't exist, -1 if auth is invalid
     */
    public static short updateGame(GameData gameData, AuthData authData)  {
        try {
            authStoredDAO.getAuth(authData);
            try {
                return gameStoredDAO.updateGame(gameData);
            } catch(DataAccessException dae) {
                return 0;
            }
        } catch(DataAccessException dae) {
            return -1;
        }
    }

    /**
     * @return success of clear
     */
    public static boolean clearGames() {
        return gameStoredDAO.clear();
    }

    public static JoinGameResponse joinGame(JoinGameRequest joinGameRequest, AuthData authData) {
        String username;
        try {
            username = authStoredDAO.getAuth(authData).username();
        } catch(DataAccessException dae) {
            return new JoinGameResponse(401);
        }

        int colorVal;
        GameData data;

        try {
            data = gameStoredDAO.getGame(new GameData(joinGameRequest.gameID(), null,null,null,null));
        } catch(DataAccessException dae) {
            return new JoinGameResponse(400);
        }

        if (joinGameRequest.playerColor() == null) {
            return new JoinGameResponse(200);
        }

        if (joinGameRequest.playerColor().equals("white") || joinGameRequest.playerColor().equals("WHITE")) {
            colorVal = 0;
            if (data.whiteUsername() != null) {
                return new JoinGameResponse(403);
            }
        } else if (joinGameRequest.playerColor().equals("black") || joinGameRequest.playerColor().equals("BLACK")) {
            colorVal = 1;
            if (data.blackUsername() != null) {
                return new JoinGameResponse(403);
            }
        } else {
            colorVal = -1;
        }

        try {
            if (colorVal >= 0) {
                GameData newGameData = new GameData(joinGameRequest.gameID(), username, username, null,null);
                gameStoredDAO.joinGame(newGameData, colorVal);
                return new JoinGameResponse(200);
            }
            return new JoinGameResponse(200);
        } catch(DataAccessException dae) {
            return new JoinGameResponse(401);
        }
    }

    public static void setGameDAO(GameDAO gameDAO) {
        gameStoredDAO = gameDAO;
    }

    public static void setAuthDAO(AuthDAO authDAO) {
        authStoredDAO = authDAO;
    }
}
