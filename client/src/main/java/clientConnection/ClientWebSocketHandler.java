package clientConnection;

import com.google.gson.Gson;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientWebSocketHandler extends Endpoint {

    private final Gson gson = new Gson();
    public Session session;

    public ClientWebSocketHandler(int port) throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://localhost:" + port + "/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler((MessageHandler.Whole<String>) System.out::println);
    }

    public void send(UserGameCommand command) throws IOException {
        String jsonMessage = gson.toJson(command);
        this.session.getBasicRemote().sendText(jsonMessage);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("ClientWebSocketHandler onOpen method called");
        System.out.println("endpointConfig: " + endpointConfig.toString());
    }
}
