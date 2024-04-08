package dataAccess;

import models.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public interface GameDAO {

    /**
     * ID is changed from null to an actual ID
     *
     * @param gameData Contains gameName, ID will change. Maybe contains a chessGame?
     * @return gameID
     */
    int createGame(GameData gameData) throws DataAccessException;

    /**
     * @param gameData Contains gameID
     * @return full GameData objects
     */
    GameData getGame(GameData gameData) throws DataAccessException, SQLException;

    /**
     * @return list of All complete GameData objects, name and ID and players
     */
    ArrayList<GameData> listGames() throws DataAccessException;

    /**
     * Adds a player to a game
     *
     * @param gameData contains gameID and the username of the player in the color they want to join
     * @param color 0 is white, 1 is black
     * @throws DataAccessException if game is not found by gameID
     */
    void joinGame(GameData gameData, int color) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    /**
     * Clears game database
     */
    void clear() throws DataAccessException;
}
