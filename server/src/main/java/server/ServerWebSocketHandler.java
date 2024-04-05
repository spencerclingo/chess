package server;

import com.google.gson.Gson;
import org.glassfish.tyrus.core.wsadl.model.Endpoint;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/connect")
public class ServerWebSocketHandler extends Endpoint {
    final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("OnConnect");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

        System.out.println("Websocket message received");

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER:
                // Add the player to the proper color
                break;
            case JOIN_OBSERVER:
                // Add the player as an observer
                break;
            case MAKE_MOVE:
                // Change the board, update it in the database, print the database version
                break;
            case LEAVE:
                // Disconnect from the websocket
                break;
            case RESIGN:
                // Send a resignation notification, disallow any further movement
                break;
        }
    }

    public void sendMessage(Session session, ServerMessage.ServerMessageType messageType) {
        ServerMessage message = new ServerMessage(messageType);
        String jsonMessage = gson.toJson(message);

        try {
            session.getBasicRemote().sendText(jsonMessage);
        } catch(IOException e) {
            System.out.println("Error in sendMessage: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("OnClose");
    }
}
