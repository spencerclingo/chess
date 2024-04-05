package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;

@WebSocket
public class ServerWebSocketHandler extends Endpoint {
    final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER:
                // Handle JOIN_PLAYER command
                break;
            case JOIN_OBSERVER:
                // Handle JOIN_OBSERVER command
                break;
            case MAKE_MOVE:
                // Handle MAKE_MOVE command
                break;
            case LEAVE:
                // Handle LEAVE command
                break;
            case RESIGN:
                // Handle RESIGN command
                break;
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {

    }

    public void sendMessage(Session session, ServerMessage.ServerMessageType messageType) {
        ServerMessage message = new ServerMessage(messageType);

        String jsonMessage = gson.toJson(message);
        try {
            session.getRemote().sendString(jsonMessage);
        } catch(IOException e) {
            System.out.println("Error in sendMessage: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(javax.websocket.Session session, EndpointConfig endpointConfig) {

    }
}
