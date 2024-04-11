package webSocketMessages.serverMessages;

import chess.ChessGame;
import models.GameData;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    private final ServerMessageType serverMessageType;
    private final String notification;
    private final GameData gameData;
    private final String username;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message, GameData gameData, String username) {
        serverMessageType = type;
        notification = message;
        this.gameData = gameData;
        this.username = username;
    }

    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public String getNotification() {
        return notification;
    }

    public GameData getGameData() {
        return gameData;
    }
    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage))
            return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
