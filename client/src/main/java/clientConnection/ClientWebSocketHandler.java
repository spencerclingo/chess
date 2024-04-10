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

        this.session.addMessageHandler((MessageHandler.Whole<String>) this::receiveMessage);
    }

    public void receiveMessage(String message) {
        System.out.println("Received message back in client");
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        GameData gameData = serverMessage.getGameData();

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                // Load the game for this player
                System.out.println(serverMessage.getNotification());
                ChessBoardPicture.init(gameData.game().getBoard(), ! username.equalsIgnoreCase(gameData.blackUsername()));
                break;
            case ERROR:
                // Print error and what error happened
                break;
            case NOTIFICATION:
                // Print out the notification
                break;
        }
    }

    public void sendMessage(UserGameCommand command) throws IOException {
        username = command.getUsername();
        String jsonMessage = gson.toJson(command);
        this.session.getBasicRemote().sendText(jsonMessage);
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
