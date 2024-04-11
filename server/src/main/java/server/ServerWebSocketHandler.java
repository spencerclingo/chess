package server;

import chess.ChessGame;
import com.google.gson.Gson;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import response.ClearResponse;
import response.GetGameResponse;
import service.WebSocketService;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class ServerWebSocketHandler {
    final Gson gson = new Gson();
    HashMap<Integer, ArrayList<Session>> gameIdToSessions = new HashMap<>();
    HashMap<Session, Integer> sessionToGameID = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("OnConnect in server");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

        System.out.println("Websocket message received");

        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER, JOIN_OBSERVER:
                int gameID = userGameCommand.getGameID();
                ArrayList<Session> sessionList = gameIdToSessions.get(gameID);
                if (sessionList == null) {
                    sessionList = new ArrayList<>();
                }
                sessionList.add(session);
                gameIdToSessions.put(gameID, sessionList);
                sessionToGameID.put(session, gameID);

                joinPlayer(userGameCommand);
                break;
            case MAKE_MOVE, RESIGN:
                makeMove(userGameCommand, session);
                break;
            case LEAVE:
                leaveMessage(userGameCommand, session);
                break;
            default:
                System.out.println("Malformed request");
        }
    }

    public void sendMessage(Session session, ServerMessage.ServerMessageType messageType, GameData game, String notification, String username) {
        ServerMessage message = new ServerMessage(messageType, notification, game, username);
        String jsonMessage = gson.toJson(message);

        try {
            session.getRemote().sendString(jsonMessage);
            System.out.println("Message sent to client");
        } catch(IOException e) {
            System.out.println("Problem sending message to client. " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("OnClose");
        System.out.println(statusCode);
        System.out.println("Reason: " + reason);
    }

    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("onError Server");
        System.out.println("Error message: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void joinPlayer(UserGameCommand userGameCommand) {
        int gameID = userGameCommand.getGameID();
        String authToken = userGameCommand.getAuthString();
        GameData gameData = new GameData(gameID, null, null, null, null);

        ArrayList<Session> sessionList = gameIdToSessions.get(gameID);
        GetGameResponse gameResponse = WebSocketService.getGame(new GetGameResponse(gameData, authToken, 1));

        String commandType;
        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER) {
            commandType = " has joined ";
        } else {
            commandType = " is observing ";
        }

        String message = userGameCommand.getUsername() + commandType + "the game!";

        for (Session session : sessionList) {
            sendMessage(session, ServerMessage.ServerMessageType.LOAD_GAME, gameResponse.gameData(), message, userGameCommand.getUsername());
        }
    }

    private void leaveMessage(UserGameCommand userGameCommand, Session session) {
        int id;
        Integer gameID = sessionToGameID.get(session);
        if (gameID != null) {
            id = gameID;
        } else {
            session.close();
            return;
        }

        ArrayList<Session> sessionList = gameIdToSessions.get(id);

        for (Session s : sessionList) {
            String notify = userGameCommand.getUsername() + " is leaving the game.";
            sendMessage(s, ServerMessage.ServerMessageType.NOTIFICATION, null, notify, userGameCommand.getUsername());
        }

        sessionList.remove(session);
        gameIdToSessions.put(id, sessionList);

        sessionToGameID.remove(session);

        session.close();
    }

    private void makeMove(UserGameCommand userGameCommand, Session session) {
        int gameID = sessionToGameID.get(session);

        GameData gameData = new GameData(gameID, null, null, null, userGameCommand.getGame());
        GetGameResponse updateResponse = new GetGameResponse(gameData, userGameCommand.getAuthString(), 0);

        ClearResponse clearResponse = WebSocketService.updateGame(updateResponse);

        ArrayList<Session> sessionList = gameIdToSessions.get(gameID);

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            if (clearResponse.httpCode() == 200) {
                for (Session s : sessionList) {
                    String notify = userGameCommand.getUsername() + " made a move!";
                    sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData, notify, userGameCommand.getUsername());
                }
            } else {
                for (Session s : sessionList) {
                    String notify = "Error: " + userGameCommand.getUsername() + " attempted to make a move but something bad happened. Either " + userGameCommand.getUsername() + " needs to log back in or the game was deleted :/";
                    sendMessage(s, ServerMessage.ServerMessageType.ERROR, null, notify, userGameCommand.getUsername());
                }
            }
        } else {
            if (clearResponse.httpCode() == 200) {
                for (Session s : sessionList) {
                    String notify = userGameCommand.getUsername() + " has resigned!";
                    sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData, notify, userGameCommand.getUsername());
                }
            } else {
                for (Session s : sessionList) {
                    String notify = "Error: " + userGameCommand.getUsername() + " attempted to resign but something bad happened. Either " + userGameCommand.getUsername() + " needs to log back in or the game was deleted :/";
                    sendMessage(s, ServerMessage.ServerMessageType.ERROR, null, notify, userGameCommand.getUsername());
                }
            }
        }
    }
}
