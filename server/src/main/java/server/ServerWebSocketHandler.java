package server;

import com.google.gson.Gson;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import response.GetGameResponse;
import service.WebSocketService;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class ServerWebSocketHandler {
    final Gson gson = new Gson();
    HashMap<Integer, ArrayList<Session>> sessionMap = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("OnConnect in server");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

        System.out.println("Websocket message received");

        int gameID = userGameCommand.getGameID();
        ArrayList<Session> sessionList = sessionMap.get(gameID);
        if (sessionList == null) {
            sessionList = new ArrayList<>();
        }
        sessionList.add(session);
        sessionMap.put(gameID, sessionList);

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER, JOIN_OBSERVER:
                joinPlayer(userGameCommand);
                break;
            case MAKE_MOVE:
                // Change the board, update it in the database, print the database version
                //break;
            case LEAVE:
                // Disconnect from the websocket
                //break;
            case RESIGN:
                // Send a resignation notification, disallow any further movement
                //break;
            default:
                // Send error message
                break;
        }
    }

    public void sendMessage(Session session, ServerMessage.ServerMessageType messageType, GameData game, String notification) {
        ServerMessage message = new ServerMessage(messageType, notification, game);
        String jsonMessage = gson.toJson(message);

        try {
            session.getRemote().sendString(jsonMessage);
            session.getRemote().sendBytes(ByteBuffer.wrap(jsonMessage.getBytes()));
            System.out.println("Message sent to client");
        } catch(IOException e) {
            System.out.println("Problem sending message to client. " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("OnClose");
        // Figure out how to get the gameID, so I know what session to remove
    }

    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("onError Server");
        System.out.println(throwable.getMessage());
        throwable.printStackTrace();
    }

    public void joinPlayer(UserGameCommand userGameCommand) {
        int gameID = userGameCommand.getGameID();
        String authToken = userGameCommand.getAuthString();
        GameData gameData = new GameData(gameID, null, null, null, null);

        ArrayList<Session> sessionList = sessionMap.get(gameID);
        GetGameResponse gameResponse = WebSocketService.getGame(new GetGameResponse(gameData, authToken, 1));

        String commandType;
        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER) {
            commandType = " has joined ";
        } else {
            commandType = " is observing ";
        }

        String message = userGameCommand.getUsername() + commandType + "the game!";

        for (Session session : sessionList) {
            sendMessage(session, ServerMessage.ServerMessageType.LOAD_GAME, gameResponse.gameData(), message);
        }
    }


}
