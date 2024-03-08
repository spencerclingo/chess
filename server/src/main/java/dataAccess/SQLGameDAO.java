package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import models.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO{

    int nextGameID = 0;
    Gson gson = new Gson();

    /**
     * ID is changed from null to an actual ID
     *
     * @param gameData Contains gameName, ID will change. Maybe contains a chessGame?
     *
     * @return gameID
     */
    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        nextGameID++;
        String statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";

        DatabaseManager.executeUpdate(statement, nextGameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        return nextGameID;
    }

    /**
     * @param gameData Contains gameID
     *
     * @return full GameData objects
     */
    @Override
    public GameData getGame(GameData gameData) throws DataAccessException, SQLException {
        int gameID = gameData.gameID();
        String whiteUsername = null;
        String blackUsername = null;
        String gameName      = null;
        String chess         = null;

        String statement = "SELECT * FROM `game` WHERE `gameID` = ?;";

        try (ResultSet resultSet = DatabaseManager.executeQuery(statement, gameID)) {
            if (resultSet.next()) {
                whiteUsername = resultSet.getString("whiteUsername");
                blackUsername = resultSet.getString("blackUsername");
                gameName      = resultSet.getString("gameName");
                chess         = resultSet.getString("chess");
            } else {
                throw new DataAccessException("No game matches gameID");
            }
            ChessGame game = null;
            if (chess != null) {
                game = gson.fromJson(chess, ChessGame.class);
            }
            return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        }
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
