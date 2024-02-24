package dataAccess;

import chess.ChessGame;
import models.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public int createGame(GameData gameData); //IDK what this one is supposed to return tbh
    public GameData getGame(int gameID);
    public ArrayList<GameData> listGames();
    public boolean updateGame(ChessGame game, int gameID);
    public boolean clear();
}
