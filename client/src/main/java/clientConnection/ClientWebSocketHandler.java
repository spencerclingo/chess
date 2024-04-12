package clientConnection;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import ui.ChessBoardPicture;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@ClientEndpoint
public class ClientWebSocketHandler extends Endpoint {

    private final Gson gson = new Gson();
    public Session session;

    public ClientWebSocketHandler(String baseUrl) throws URISyntaxException, DeploymentException, IOException {
        String replaced = baseUrl.replace("http", "ws");
        String uriString = replaced + "connect";
        URI uri = new URI(uriString);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                receiveMessage(message);
            }
        });
    }

    public void receiveMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        ChessGame game = serverMessage.getGame();

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                System.out.println(serverMessage.getMessage());
                if (ClientMenu.getColor().equals("black")) {
                    ChessBoardPicture.init(game.getBoard(), false, new ArrayList<>(), null);
                } else {
                    ChessBoardPicture.init(game.getBoard(), true, new ArrayList<>(), null);
                }
                ClientMenu.saveGame(serverMessage.getGame());
                break;
            case ERROR:
                System.out.println(serverMessage.getMessage());

                if (serverMessage.getUsername().equalsIgnoreCase(ClientMenu.savedUsername)) {
                    UserGameCommand command = new UserGameCommand(null, UserGameCommand.CommandType.LEAVE, 0, ClientMenu.savedUsername, null);
                    try {
                        sendMessage(command);
                    } catch(Exception e) {
                        System.out.println("When trying to remove you from the game, an error occurred while communicating to the server. ");
                    }
                }
                break;
            case NOTIFICATION:
                System.out.println(serverMessage.getMessage());
                break;
        }
    }

    public void sendMessage(UserGameCommand command) throws IOException {
        ClientMenu.savedUsername = command.getUsername();
        String jsonMessage = gson.toJson(command);
        this.session.getBasicRemote().sendText(jsonMessage);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    @OnError
    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error in ClientWebSocketHandler");
        System.out.println(t.getMessage());
    }

    @OnClose
    @OnWebSocketClose
    public void onClose() {
        System.out.println("Client ws closed");
        ClientMenu.savedUsername = "";
        session = null;
    }
}
