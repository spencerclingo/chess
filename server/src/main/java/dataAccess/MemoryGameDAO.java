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
     * @return full GameData object
     */
    @Override
    public GameData getGame(GameData gameData) {
        int gameID = gameData.gameID();

        return gameMap.get(gameID);
    }

    /**
     * @return list of All complete GameData objects, name and ID and players
     */
    @Override
    public ArrayList<GameData> listGames() {
        Collection<GameData> values = gameMap.values();
        return new ArrayList<>(values);
    }

    /**
     * @param gameData Contains ChessGame and gameID
     * @return boolean
     */
    @Override
    public boolean updateGame(GameData gameData) {
        ChessGame newGame = gameData.game();
        int gameID = gameData.gameID();

        if (gameMap.get(gameID) == null) {
            return false;
        }
        gameMap.put(gameID, gameMap.get(gameID).copyChangedGame(newGame));
        return true;
    }

    /**
     * @return Bool of if it was a successful clear
     */
    @Override
    public boolean clear() {
        gameMap.clear();
        return true;
    }
}
