package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    protected CommandType commandType;
    private final String authToken;
    private final int gameID;
    private final String username;
    private final ChessGame game;
    private final String playerColor;
    private final ChessMove move;

    public UserGameCommand(String authToken, CommandType type, int gameID, String username, ChessGame game) {
        this.authToken = authToken;
        commandType = type;
        this.gameID = gameID;
        this.username = username;
        this.game = game;
        this.playerColor = "";
        move = null;
    }

    public UserGameCommand(String authToken, CommandType type, int gameID, String username, ChessGame game, String playerColor) {
        this.authToken = authToken;
        commandType = type;
        this.gameID = gameID;
        this.username = username;
        this.game = game;
        this.playerColor = playerColor;
        move = null;
    }

    public UserGameCommand(String authToken, CommandType type, int gameID, String username, ChessGame game, String playerColor, ChessMove move) {
        this.authToken = authToken;
        commandType = type;
        this.gameID = gameID;
        this.username = username;
        this.game = game;
        this.playerColor = playerColor;
        this.move = move;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public ChessMove getMove() {
        return move;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public int getGameID() {
        return gameID;
    }

    public String getUsername() {
        return username;
    }

    public ChessGame getGame() {
        return game;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}
