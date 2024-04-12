package webSocketMessages.serverMessages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    private final ChessGame game;
    private final ServerMessageType serverMessageType;
    private final String message;
    private final String username;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message, ChessGame game, String username) {
        serverMessageType = type;
        this.message = message;
        this.game = game;
        this.username = username;
    }

    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return game;
    }
    public String getUsername() {
        return username;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerMessage message = (ServerMessage) o;
        return getServerMessageType() == message.getServerMessageType() && Objects.equals(getMessage(), message.getMessage()) && Objects.equals(getGame(), message.getGame()) && Objects.equals(getUsername(), message.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
