package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;

@WebSocket
public class ServerWebSocketHandler {
    final Gson gson = new Gson();
    ArrayList<Session> sessions = new ArrayList<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("OnConnect in server");
        sessions.add(session);
    }

    @OnWebSocketMessage
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
            session.getRemote().sendString(jsonMessage);
        } catch(IOException e) {
            System.out.println("Error in sendMessage: " + e.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("OnClose");
    }

}
