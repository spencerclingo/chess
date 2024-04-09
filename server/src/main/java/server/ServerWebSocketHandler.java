package server;

import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import response.GetGameResponse;
import response.WebSocketJoinGameResponse;
import service.WebSocketService;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class ServerWebSocketHandler {
    final Gson gson = new Gson();
    HashMap<Integer, ArrayList<Session>> sessionMap = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("OnConnect in server");
        int gameID = Integer.parseInt(session.getUpgradeRequest().getParameterMap().get("gameID").getFirst());
        ArrayList<Session> sessionList = sessionMap.get(gameID);
        if (sessionList == null) {
            sessionList = new ArrayList<>();
        }
        sessionList.add(session);
        sessionMap.put(gameID, sessionList);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

        System.out.println("Websocket message received");

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER, JOIN_OBSERVER:
                joinPlayer(userGameCommand, session);
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
        // Figure out how to get the gameID, so I know what session to remove
    }

    public void joinPlayer(UserGameCommand userGameCommand, Session currentSession) {
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
        WebSocketJoinGameResponse joinGameResponse = new WebSocketJoinGameResponse(gameResponse.gameData().game(), message);

        String jsonString = gson.toJson(joinGameResponse);

        for (Session session : sessionList) {
            try {
                session.getRemote().sendString(jsonString);
            } catch (IOException e) {
                System.out.println("Problem sending message to client in gameID: " + gameID);
                e.printStackTrace();
            }
        }
    }


}
