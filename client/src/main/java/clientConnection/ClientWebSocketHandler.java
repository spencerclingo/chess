package clientConnection;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;


import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//mvn install gives an error about something not implementing WebSocketListener or being annotated with @WebSocket

public class ClientWebSocketHandler extends Endpoint {

    private final Gson gson = new Gson();
    public Session session;

    public ClientWebSocketHandler(String baseUrl) throws URISyntaxException, DeploymentException, IOException {
        String replaced = baseUrl.replace("http", "ws");
        String uriString = replaced + "connect";
        URI uri = new URI(uriString);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }


    public void onMessage(String message) {
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

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("ClientWebSocketHandler onOpen method called");
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println(t.getMessage());
    }

    @OnClose
    public void onClose() {
        System.out.println("Client ws closed");
    }
}
