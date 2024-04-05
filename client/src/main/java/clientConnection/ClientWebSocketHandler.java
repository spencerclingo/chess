package clientConnection;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//mvn install gives an error about something not implementing WebSocketListener or being annotated with @WebSocket

@ClientEndpoint
public class ClientWebSocketHandler {

    private final Gson gson = new Gson();
    public Session session;

    public ClientWebSocketHandler(int port) throws URISyntaxException, DeploymentException, IOException {
        System.out.println(0);
        System.out.println(port);
        String uriString = "ws://localhost:" + port + "/connect";
        System.out.println(uriString);
        URI uri = new URI(uriString);
        System.out.println(1);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        System.out.println(2);
        this.session = container.connectToServer(this, uri);
        System.out.println(3);

        this.session.addMessageHandler((MessageHandler.Whole<String>) System.out::println);
        System.out.println(4);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                // Load the game for all players
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
        String jsonMessage = gson.toJson(command);
        this.session.getBasicRemote().sendText(jsonMessage);
    }

    @OnOpen
    public void onOpen() {
        System.out.println("ClientWebSocketHandler onOpen method called");
    }

    @OnClose
    public void onClose (Session session) {

    }
}
