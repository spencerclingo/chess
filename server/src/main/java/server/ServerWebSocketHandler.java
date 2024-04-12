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

                joinPlayer(userGameCommand, session);
                break;
            case MAKE_MOVE, RESIGN:
                makeMove(userGameCommand, session);
                break;
            case LEAVE:
                leaveMessage(userGameCommand, session, true);
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

        leaveMessage(new UserGameCommand(null,null, -1, null,null), session, false);
    }

    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("onError Server");
        System.out.println("Error message: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void joinPlayer(UserGameCommand userGameCommand, Session thisSession) {
        int gameID = userGameCommand.getGameID();
        String authToken = userGameCommand.getAuthString();
        GameData gameData = new GameData(gameID, null, null, null, null);

        ArrayList<Session> sessionList = gameIdToSessions.get(gameID);
        GetGameResponse gameResponse = WebSocketService.getGame(new GetGameResponse(gameData, authToken,null, 1));

        String commandType = joinNotification(userGameCommand, gameResponse);

        System.out.println(gameResponse.gameData().game());

        for (Session session : sessionList) {
            if (session.equals(thisSession)) {
                System.out.println("Sending the game");
                sendMessage(session, ServerMessage.ServerMessageType.LOAD_GAME, gameResponse.gameData(), "", userGameCommand.getUsername());
            } else {
                sendMessage(session, ServerMessage.ServerMessageType.NOTIFICATION, null, commandType, userGameCommand.getUsername());
            }
        }
    }

    private static String joinNotification(UserGameCommand userGameCommand, GetGameResponse gameResponse) {
        String message = "the game";

        String commandType;
        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.JOIN_PLAYER) {
            commandType = userGameCommand.getUsername() + " has joined " + message + " as ";

            if (gameResponse.gameData().whiteUsername() != null && gameResponse.gameData().whiteUsername().equals(userGameCommand.getUsername())) {
                commandType = commandType + "white!";
            } else {
                commandType = commandType + "black!";
            }
        } else {
            System.out.println("joinNotification");
            commandType = userGameCommand.getUsername() + " is observing " + message + "!";
        }
        return commandType;
    }

    private void leaveMessage(UserGameCommand userGameCommand, Session session, boolean onPurpose) {
        int id;
        Integer gameID = sessionToGameID.get(session);
        if (gameID != null) {
            id = gameID;
        } else {
            session.close();
            return;
        }

        GameData gameData = new GameData(id, null,null, null,null);
        GetGameResponse getGameResponse = new GetGameResponse(gameData, userGameCommand.getAuthString(), userGameCommand.getUsername(), 0);
        //ClearResponse clearResponse = WebSocketService.playerLeaves(getGameResponse);

        //if (clearResponse.httpCode() != 200) {
        //    String notify = "Error: Either your authToken failed or you aren't in this game";
        //    sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, notify, userGameCommand.getUsername());
        //}

        ArrayList<Session> sessionList = gameIdToSessions.get(id);

        if (onPurpose) {
            for (Session s : sessionList) {
                if (! s.equals(session)) {
                    String notify = userGameCommand.getUsername() + " is leaving the game.";
                    sendMessage(s, ServerMessage.ServerMessageType.NOTIFICATION, null, notify, userGameCommand.getUsername());
                }
            }
        }

        sessionList.remove(session);
        gameIdToSessions.put(id, sessionList);

        sessionToGameID.remove(session);

        session.close();
    }

    private void makeMove(UserGameCommand userGameCommand, Session session) {
        if (gameOver(userGameCommand, session)) {
            String notify = "Game is over, no moves can be played!";
            sendMessage(session, ServerMessage.ServerMessageType.NOTIFICATION, null, notify, userGameCommand.getUsername());
        }

        int gameID = sessionToGameID.get(session);

        GameData gameData = new GameData(gameID, null, null, null, userGameCommand.getGame());
        GetGameResponse updateResponse = new GetGameResponse(gameData, userGameCommand.getAuthString(), null, 0);

        ChessGame game = userGameCommand.getGame();

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            game.setGameOver(true);
        }

        ClearResponse clearResponse = WebSocketService.updateGame(updateResponse);

        ArrayList<Session> sessionList = gameIdToSessions.get(gameID);

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            if (clearResponse.httpCode() == 200) {
                for (Session s : sessionList) {
                    String notify = userGameCommand.getUsername() + " made a move!";
                    if (game.isInCheckmate(game.getTeamTurn())) {
                        notify += "\nCheckmate!";
                    } else if (game.isInCheck(game.getTeamTurn())) {
                        notify += "\nCheck!";
                    } else if (game.isInStalemate(game.getTeamTurn())) {
                        notify += "\nStalemate!";
                        game.setGameOver(true);
                    }

                    if (session.equals(s)) {
                        sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData, "", userGameCommand.getUsername());
                    } else {
                        sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData, notify, userGameCommand.getUsername());
                    }
                }
            } else {
                String notify = "Error: " + userGameCommand.getUsername() + " attempted to make a move but something bad happened. Either " + userGameCommand.getUsername() + " needs to log back in or the game was deleted :/";
                sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, notify, userGameCommand.getUsername());
            }
        } else {
            if (clearResponse.httpCode() == 200) {
                for (Session s : sessionList) {
                    String notify = userGameCommand.getUsername() + " has resigned!";
                    sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData, notify, userGameCommand.getUsername());
                }
            } else {
                String notify = "Error: " + userGameCommand.getUsername() + " attempted to resign but something bad happened. Either " + userGameCommand.getUsername() + " needs to log back in or the game was deleted :/";
                sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, notify, userGameCommand.getUsername());
            }
        }
    }

    private boolean gameOver(UserGameCommand userGameCommand, Session session) {
        return userGameCommand.getGame().getGameOver();
    }
}
