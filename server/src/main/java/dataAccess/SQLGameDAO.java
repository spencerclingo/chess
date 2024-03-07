package dataAccess;

import models.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO{
    /**
     * ID is changed from null to an actual ID
     *
     * @param gameData Contains gameName, ID will change. Maybe contains a chessGame?
     *
     * @return gameID
     */
    @Override
    public int createGame(GameData gameData) {
        return 0;
    }

    /**
     * @param gameData Contains gameID
     *
     * @return full GameData objects
     */
    @Override
    public GameData getGame(GameData gameData) throws DataAccessException {
        return null;
    }

    /**
     * @return list of All complete GameData objects, name and ID and players
     */
    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    /**
     * Adds a player to a game
     *
     * @param gameData contains gameID and the username of the player in the color they want to join
     * @param color    0 is white, 1 is black
     *
     * @throws DataAccessException if game is not found by gameID
     */
    @Override
    public void joinGame(GameData gameData, int color) throws DataAccessException {

    }

    /**
     * Clears game database
     */
    @Override
    public void clear() {

    }
}
