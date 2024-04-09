package service;

import dataAccess.*;
import models.AuthData;
import models.GameData;
import response.*;

import java.sql.SQLException;

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
            System.out.println(dae.getMessage());
            return new CreateGameResponse(-1, 401);
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
     * Clears game database
     */
    public static boolean clearGames() {
        try {
            gameStoredDAO.clear();
        } catch (DataAccessException dae) {
            return false;
        }
        return true;
    }

    /**
     * Adds the player attached to the authCode to the color specified in the joinGameRequest to the game at the gameID
     *
     * @param joinGameRequest PlayerColor (can be empty) and gameID
     * @param authData contains authToken
     * @return JoinGameResponse with httpCode
     */
    public static JoinGameResponse joinGame(JoinGameRequest joinGameRequest, AuthData authData) throws DataAccessException {
        String username = authStoredDAO.getAuth(authData).username();

        int colorVal=-1;
        GameData data;

        try {
            data = gameStoredDAO.getGame(new GameData(joinGameRequest.gameID(), null,null,null,null));
        } catch(DataAccessException | SQLException dae) {
            return new JoinGameResponse(400);
        }

        if (joinGameRequest.playerColor() == null || (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("white") && !joinGameRequest.playerColor().equals("BLACK") && !joinGameRequest.playerColor().equals("black"))) {
            try {
                GameData newGameData = new GameData(joinGameRequest.gameID(), null, null, null,null);
                newGameData = gameStoredDAO.getGame(newGameData);
                if ((newGameData.blackUsername() != null && newGameData.blackUsername().equals(username)) || (newGameData.whiteUsername() != null && newGameData.whiteUsername().equals(username))) {
                    return new JoinGameResponse(400);
                }
                return new JoinGameResponse(200);
            } catch(DataAccessException | SQLException dae) {
                return new JoinGameResponse(400);
            }
        }

        if (joinGameRequest.playerColor().equals("white") || joinGameRequest.playerColor().equals("WHITE")) {
            colorVal = 0;
            if (data.whiteUsername() != null) {
                if (data.whiteUsername().equalsIgnoreCase(joinGameRequest.playerColor())) {
                    return new JoinGameResponse(201);
                }
                return new JoinGameResponse(403);
            }
        }
        if (joinGameRequest.playerColor().equals("black") || joinGameRequest.playerColor().equals("BLACK")) {
            colorVal = 1;
            if (data.blackUsername() != null) {
                if (data.blackUsername().equalsIgnoreCase(joinGameRequest.playerColor())) {
                    return new JoinGameResponse(201);
                }
                return new JoinGameResponse(403);
            }
        }

        try {
            GameData newGameData = new GameData(joinGameRequest.gameID(), username, username, null,null);
            gameStoredDAO.joinGame(newGameData, colorVal);
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
