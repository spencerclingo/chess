package dataAccess;

import models.GameData;

import java.util.ArrayList;

public interface GameDAO {
    int createGame(GameData gameData); //IDK what this one is supposed to return tbh
    GameData getGame(GameData gameData) throws DataAccessException;
    ArrayList<GameData> listGames();
    void joinGame(GameData gameData, int color) throws DataAccessException;
    void clear();
}
