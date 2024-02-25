package dataAccess;

import chess.ChessGame;
import models.GameData;

import java.util.ArrayList;
import java.util.Collection;
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
        GameData newGameData = gameData.copyChangedID(nextGameID);
        nextGameID++;

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
     * @param gameData Must contain GameID, other things can change
     * @return boolean
     */
    @Override
    public short updateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();

        if (gameMap.get(gameID) == null) {
            throw new DataAccessException("Game not found");
        }
        gameMap.put(gameID, gameData);
        return 1;
    }

    /**
     * @return Bool of if it was a successful clear
     */
    @Override
    public boolean clear() {
        gameMap.clear();
        nextGameID = 0;
        return true;
    }
}
