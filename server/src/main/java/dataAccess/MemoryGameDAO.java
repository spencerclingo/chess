package dataAccess;

import chess.ChessGame;
import models.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    int nextGameID = 0;
    HashMap<Integer, GameData> gameMap = new HashMap<>();


    @Override
    public int createGame(GameData gameData) {
        GameData newGameData = gameData.copyChangedID(nextGameID);
        nextGameID++;

        gameMap.put(newGameData.gameID(), newGameData);
        return newGameData.gameID();
    }

    @Override
    public GameData getGame(int gameID) {
        return gameMap.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGames() {
        Collection<GameData> values = gameMap.values();
        return new ArrayList<>(values);
    }

    @Override
    public boolean updateGame(ChessGame newGame, int gameID) {
        if (gameMap.get(gameID) == null) {
            return false;
        }
        gameMap.put(gameID, gameMap.get(gameID).copyChangedGame(newGame));
        return true;
    }

    @Override
    public boolean clear() {
        gameMap.clear();
        return true;
    }
}
