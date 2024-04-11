package clientConnection;

import com.google.gson.Gson;
import models.GameData;
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
    String username = "";

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
        System.out.println("Received message back in client");
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        GameData gameData = serverMessage.getGameData();

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                System.out.println(serverMessage.getNotification());
                ChessBoardPicture.init(gameData.game().getBoard(), ! username.equalsIgnoreCase(gameData.blackUsername()), new ArrayList<>(), null);
                ClientMenu.saveGame(serverMessage.getGameData().game());
                break;
            case ERROR:
                System.out.println(serverMessage.getNotification());

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
                System.out.println(serverMessage.getNotification());
                break;
        }
    }

    public void sendMessage(UserGameCommand command) throws IOException {
        username = command.getUsername();
        String jsonMessage = gson.toJson(command);
        this.session.getBasicRemote().sendText(jsonMessage);
        System.out.println("message sent");
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("ClientWebSocketHandler onOpen method called");
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("Error in ClientWebSocketHandler");
        System.out.println(t.getMessage());
    }

    @OnClose
    public void onClose() {
        System.out.println("Client ws closed");
        username = "";
    }
}
