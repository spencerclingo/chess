package dataAccess;

import models.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    int nextGameID = 0;
    HashMap<Integer, GameData> gameMap = new HashMap<>();


    /**
     * ID is changed from null to an actual ID
     *
     * @param gameData Contains gameName, ID will change. Maybe contains a chessGame?
     * @return gameID
     */
    @Override
    public int createGame(GameData gameData) {
        nextGameID++;
        GameData newGameData = gameData.copyChangedID(nextGameID);

        gameMap.put(newGameData.gameID(), newGameData);
        return newGameData.gameID();
    }

    /**
     * @param gameData Contains gameID
     * @return full GameData objects
     */
    @Override
    public GameData getGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();

        if (gameMap.get(gameID) == null) {
            throw new DataAccessException("No game with that gameID exists");
        }

        return gameMap.get(gameID);
    }

    /**
     * @return list of All complete GameData objects, name and ID and players
     */
    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameMap.values());
    }

    /**
     * Adds a player to a game
     *
     * @param gameData contains gameID and the username of the player in the color they want to join
     * @param color 0 is white, 1 is black
     * @throws DataAccessException if game is not found by gameID
     */
    public void joinGame(GameData gameData, int color) throws DataAccessException {
        int id = gameData.gameID();
        GameData oldData = gameMap.get(id);

        if (oldData == null) {
            throw new DataAccessException("Game with that ID cannot be joined");
        }

        if (color == 0) {
            gameMap.put(id, new GameData(id, gameData.whiteUsername(), oldData.blackUsername(), oldData.gameName(), oldData.game()));
        } else if (color == 1) {
            gameMap.put(id, new GameData(id, oldData.whiteUsername(), gameData.blackUsername(), oldData.gameName(), oldData.game()));
        }
    }

    /**
     * @return Bool of if it was a successful clear
     */
    @Override
    public void clear() {
        gameMap.clear();
        nextGameID = 0;
    }
}
