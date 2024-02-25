package dataAccess;

import models.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public int createGame(GameData gameData); //IDK what this one is supposed to return tbh
    public GameData getGame(GameData gameData) throws DataAccessException;
    public ArrayList<GameData> listGames();
    public short updateGame(GameData gameData) throws DataAccessException;
    public boolean joinGame(GameData gameData, int color) throws DataAccessException;
    public boolean clear();
}
