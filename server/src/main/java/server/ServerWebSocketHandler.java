package server;

import chess.ChessGame;
import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import response.ClearResponse;
import response.GetGameResponse;
import response.JoinGameRequest;
import response.JoinGameResponse;
import service.AuthService;
import service.GameService;
import service.WebSocketService;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@WebSocket
public class ServerWebSocketHandler {
    final Gson gson = new Gson();
    HashMap<Integer, ArrayList<Session>> gameIdToSessions = new HashMap<>();
    HashMap<Session, Integer> sessionToGameID = new HashMap<>();

    @OnOpen
    @OnWebSocketConnect
    public void onConnect(Session session) {
    }

    @OnMessage
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

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

    public void sendMessage(Session session, ServerMessage.ServerMessageType messageType, ChessGame game, String notification, String username) {
        ServerMessage message;
        if (messageType != ServerMessage.ServerMessageType.ERROR) {
            message = new ServerMessage(messageType, notification, game, username);
        } else {
            message = new ServerMessage(messageType, "", game, username, notification);
        }

        String jsonMessage = gson.toJson(message);

        try {
            session.getRemote().sendString(jsonMessage);
        } catch(IOException e) {
            System.out.println("Problem sending message to client. " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClose
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        leaveMessage(new UserGameCommand(null,null, -1, null,new ChessGame()), session, false);
    }

    @OnError
    @OnWebSocketError
    public void onError(Throwable throwable) {
        System.out.println("onError Server");
        System.out.println("Error message: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void joinPlayer(UserGameCommand userGameCommand, Session thisSession) {
        int gameID = userGameCommand.getGameID();
        String authToken = userGameCommand.getAuthString();

        String username = AuthService.getAuth(new AuthData(authToken, null)).username();

        GameData gameData = new GameData(gameID, null, null, null, new ChessGame());

        ArrayList<Session> sessionList = gameIdToSessions.get(gameID);

        //JoinGameRequest joinRequest = new JoinGameRequest(userGameCommand.getPlayerColor(), gameID);
        JoinGameResponse joinResponse;
        /*try {
            joinResponse = GameService.joinGame(joinRequest, new AuthData(authToken, username));
        } catch(Exception e) {
            System.out.println("Something went wrong");
            sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: Something went wrong", null);
            return;
        }

        if (joinResponse.httpCode() == 401 || username == null) {
            System.out.println("Unauthorized");
            sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: Unauthorized", null);
            return;
        } else if (joinResponse.httpCode() == 400) {
            System.out.println(gameID);
            System.out.println("No game at ID");
            sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: No game at that ID", null);
            return;
        } else if (joinResponse.httpCode() == 403) {
            System.out.println("Already taken");
            sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: already taken", null);
            return;
        }

         */

        GetGameResponse gameResponse = WebSocketService.getGame(new GetGameResponse(gameData, authToken,null, 1));

        if (gameResponse.gameData() == null) {
            System.out.println("Bad game ID");
            sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: Bad gameID", null);
            return;
        }

        if (userGameCommand.getPlayerColor().equalsIgnoreCase("white")) {
            if (gameResponse.gameData().whiteUsername() == null || !gameResponse.gameData().whiteUsername().equalsIgnoreCase(username)) {
                System.out.println("Did not join");
                sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: did not join the game", null);
                return;
            }
        } else if (userGameCommand.getPlayerColor().equalsIgnoreCase("black")) {
            if (gameResponse.gameData().blackUsername() == null || !gameResponse.gameData().blackUsername().equalsIgnoreCase(username)) {
                System.out.println("Did not join");
                sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: did not join the game", null);
                return;
            }
        }

        String commandType = joinNotification(userGameCommand, gameResponse);

        for (Session session : sessionList) {
            if (session.equals(thisSession)) {
                sendMessage(session, ServerMessage.ServerMessageType.LOAD_GAME, gameResponse.gameData().game(), "", username);
            } else {
                sendMessage(session, ServerMessage.ServerMessageType.NOTIFICATION, gameResponse.gameData().game(), commandType, username);
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

        GameData gameData = new GameData(id, null,null, null,new ChessGame());
        GetGameResponse getGameResponse = new GetGameResponse(gameData, userGameCommand.getAuthString(), userGameCommand.getUsername(), 0);

        ArrayList<Session> sessionList = gameIdToSessions.get(id);

        if (onPurpose) {
            for (Session s : sessionList) {
                if (! s.equals(session)) {
                    String notify = userGameCommand.getUsername() + " is leaving the game.";
                    sendMessage(s, ServerMessage.ServerMessageType.NOTIFICATION, new ChessGame(), notify, userGameCommand.getUsername());
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
            sendMessage(session, ServerMessage.ServerMessageType.NOTIFICATION, new ChessGame(), notify, userGameCommand.getUsername());
            return;
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
                        sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData.game(), "", userGameCommand.getUsername());
                    } else {
                        sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData.game(), notify, userGameCommand.getUsername());
                    }
                }
            } else {
                String notify = "Error: " + userGameCommand.getUsername() + " attempted to make a move but something bad happened. Either " + userGameCommand.getUsername() + " needs to log back in or the game was deleted :/";
                sendMessage(session, ServerMessage.ServerMessageType.ERROR, new ChessGame(), notify, userGameCommand.getUsername());
            }
        } else {
            if (clearResponse.httpCode() == 200) {
                for (Session s : sessionList) {
                    String notify = userGameCommand.getUsername() + " has resigned!";
                    sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, gameData.game(), notify, userGameCommand.getUsername());
                }
            } else {
                String notify = "Error: " + userGameCommand.getUsername() + " attempted to resign but something bad happened. Either " + userGameCommand.getUsername() + " needs to log back in or the game was deleted :/";
                sendMessage(session, ServerMessage.ServerMessageType.ERROR, new ChessGame(), notify, userGameCommand.getUsername());
            }
        }
    }

    private boolean gameOver(UserGameCommand userGameCommand, Session session) {
        return userGameCommand.getGame().getGameOver();
    }
}
