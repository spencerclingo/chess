package service;

import dataAccess.*;
import models.AuthData;
import models.GameData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class GameService {
    static MemoryGameDAO gameStoredDAO = new MemoryGameDAO();
    static MemoryAuthDAO authStoredDAO = AuthService.authStoredDAO;

    /**
     * @param gameData contains gameName. Can contain chessGame
     * @return gameID or -1 to show authToken doesn't exist
     */
    public static int createGame(GameData gameData, AuthData authData) {
        try {
            authStoredDAO.getAuth(authData);
            return gameStoredDAO.createGame(gameData);
        } catch(DataAccessException dae) {
            return -1;
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
    public static ArrayList<GameData> listGames(AuthData authData) {
        try {
            authStoredDAO.getAuth(authData);
            return gameStoredDAO.listGames();
        } catch(DataAccessException dae) {
            return null;
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

    public static boolean joinGame(GameData gameData, AuthData authData) {
        try {
            authStoredDAO.getAuth(authData);
        } catch(DataAccessException dae) {
            return false;
        }

        int colorVal;

        if (gameData.whiteUsername() != null) {
            colorVal = 0;
        } else {
            colorVal = 1;
        }

        try {
            return gameStoredDAO.joinGame(gameData, colorVal);
        } catch(DataAccessException dae) {
            return false;
        }
    }

    public static void setGameDAO(MemoryGameDAO gameDAO) {
        gameStoredDAO = gameDAO;
    }

    public static void setAuthDAO(MemoryAuthDAO authDAO) {
        authStoredDAO = authDAO;
    }
}
