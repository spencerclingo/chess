package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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

    @OnWebSocketConnect
    public void onConnect(Session session) {
    }

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

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        leaveMessage(new UserGameCommand(null,null, -1, null,new ChessGame()), session, false);
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

        AuthData authData = verifyAuth(thisSession, authToken);

        if (authData == null) {
            return;
        }

        String username = authData.username();

        GameData gameData = new GameData(gameID, null, null, null, new ChessGame());

        ArrayList<Session> sessionList = gameIdToSessions.get(gameID);

        GetGameResponse gameResponse = WebSocketService.getGame(new GetGameResponse(gameData, authToken,null, 1));

        if (gameResponse.gameData() == null) {
            System.out.println("Bad game ID");
            sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: Bad gameID", null);
            return;
        }

        if (userGameCommand.getPlayerColor() != null) {
            if (userGameCommand.getPlayerColor().equalsIgnoreCase("white")) {
                if (gameResponse.gameData().whiteUsername() == null || ! gameResponse.gameData().whiteUsername().equalsIgnoreCase(username)) {
                    System.out.println("Did not join");
                    sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: did not join the game", null);
                    return;
                }
            } else if (userGameCommand.getPlayerColor().equalsIgnoreCase("black")) {
                if (gameResponse.gameData().blackUsername() == null || ! gameResponse.gameData().blackUsername().equalsIgnoreCase(username)) {
                    System.out.println("Did not join");
                    sendMessage(thisSession, ServerMessage.ServerMessageType.ERROR, null, "Error: did not join the game", null);
                    return;
                }
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
        GameData gameData = new GameData(userGameCommand.getGameID(), null,null,null,null);
        GetGameResponse getGameResponse = WebSocketService.getGame(new GetGameResponse(gameData, userGameCommand.getAuthString(), null, 0));
        gameData = getGameResponse.gameData();

        ChessGame game = gameData.game();

        if (gameOver(gameData, session)) {
            String notify = "Game is over, no moves can be played!";
            sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, notify, userGameCommand.getUsername());
            return;
        }

        int gameID = sessionToGameID.get(session);

        GameData gameData1 = new GameData(gameID, null, null, null, game);
        GetGameResponse updateResponse = new GetGameResponse(gameData1, userGameCommand.getAuthString(), null, 0);

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.RESIGN) {
            game.setGameOver(true);
        }

        ChessMove move = null;

        String authToken = userGameCommand.getAuthString();
        AuthData authData = verifyAuth(session, authToken);

        if (authData == null) {
            return;
        }
        String username = authData.username();

        if (userGameCommand.getMove() != null) {
            move = userGameCommand.getMove();
            if (game.getTeamTurn() != game.getBoard().getPiece(move.getStartPosition()).getTeamColor()) {
                System.out.println("Wrong turn");
                sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, "Error: wrong turn", null);
                return;
            }

            ChessGame.TeamColor teamColor = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();

            if (teamColor == ChessGame.TeamColor.WHITE) {
                if (!gameData.whiteUsername().equalsIgnoreCase(username)) {
                    System.out.println("Not your piece");
                    sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, "Error: Not your piece", null);
                    return;
                }
            } else {
                if (gameData.blackUsername().equalsIgnoreCase(username)) {
                    System.out.println("Not your piece");
                    sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, "Error: Not your piece", null);
                    return;
                }
            }
        }

        if (!username.equalsIgnoreCase(gameData.whiteUsername()) && !username.equalsIgnoreCase(gameData.blackUsername())) {
            System.out.println("Have to be a player to resign");
            sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, "Error: Have to be a player to resign", null);
            return;
        }

        try {
            game.makeMove(move);
        } catch(InvalidMoveException ime) {
            System.out.println("Invalid move");
            sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, "Error: invalid move", null);
            return;
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
                        sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, game, "", userGameCommand.getUsername());
                    } else {
                        sendMessage(s, ServerMessage.ServerMessageType.LOAD_GAME, game, "", userGameCommand.getUsername());
                        sendMessage(s, ServerMessage.ServerMessageType.NOTIFICATION, null, notify, userGameCommand.getUsername());
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
                    sendMessage(s, ServerMessage.ServerMessageType.NOTIFICATION, game, notify, userGameCommand.getUsername());
                    game.setGameOver(true);
                }
            } else {
                String notify = "Error: " + userGameCommand.getUsername() + " attempted to resign but something bad happened. Either " + userGameCommand.getUsername() + " needs to log back in or the game was deleted :/";
                sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, notify, userGameCommand.getUsername());
            }
        }
    }

    private AuthData verifyAuth(Session session, String authToken) {
        AuthData authData = AuthService.getAuth(new AuthData(authToken, null));

        if (authData == null) {
            System.out.println("Bad authToken");
            sendMessage(session, ServerMessage.ServerMessageType.ERROR, null, "Error: Bad authToken", null);
            return null;
        }

        return authData;
    }

    private boolean gameOver(GameData gameData, Session session) {
        return gameData.game().getGameOver();
    }
}
