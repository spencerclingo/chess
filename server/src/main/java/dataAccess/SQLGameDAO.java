package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import models.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO{

    int nextGameID = 0;
    final Gson gson = new Gson();

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

        DatabaseManager.executeUpdate(statement, nextGameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gson.toJson(gameData.game()));
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
        String whiteUsername;
        String blackUsername;
        String gameName;
        String chess;

        String statement = "SELECT * FROM `game` WHERE `gameID` = ?;";

        try (ResultSet resultSet = DatabaseManager.executeQuery(statement, gameID)) {
            if (resultSet.next()) {
                whiteUsername = resultSet.getString("whiteUsername");
                blackUsername = resultSet.getString("blackUsername");
                gameName      = resultSet.getString("gameName");
                chess         = resultSet.getString("game");
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
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> listOfGames = new ArrayList<>();

        String statement = "SELECT * FROM `game`;";

        try (ResultSet resultSet = DatabaseManager.executeQuery(statement)) {
            while (resultSet.next()) {
                int gameID           = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName      = resultSet.getString("gameName");
                String chess         = resultSet.getString("game");

                if (chess != null) {
                    listOfGames.add(new GameData(gameID, whiteUsername, blackUsername, gameName, gson.fromJson(chess, ChessGame.class)));
                } else {
                    listOfGames.add(new GameData(gameID, whiteUsername, blackUsername, gameName, null));
                }
            }
            return listOfGames;
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
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
        int gameID = gameData.gameID();

        String statement;
        String username;

        if (color == 0) {
            username = gameData.whiteUsername();
            statement = "UPDATE `game` SET `whiteUsername` = ? WHERE `gameID` = ?;";
        } else if (color == 1) {
            username = gameData.blackUsername();
            statement = "UPDATE `game` SET `blackUsername` = ? WHERE `gameID` = ?;";
        } else {
            return;
        }

        DatabaseManager.executeUpdate(statement, username, gameID);
    }

    //Unused

    /**
     * Updates a game with given gameID to given ChessGame
     *
     * @param gameData Contains gameID and new ChessGame
     * @throws DataAccessException if game doesn't exist
     */
    public void updateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.gameID();
        ChessGame newGame = gameData.game();

        String statement = "UPDATE `game` SET `game` = ? WHERE `gameID` = ?;";

        DatabaseManager.executeUpdate(statement, newGame, gameID);
    }

    /**
     * Clears game database
     */
    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM `game`;";

        DatabaseManager.executeUpdate(statement);

        nextGameID = 0;
    }
}
