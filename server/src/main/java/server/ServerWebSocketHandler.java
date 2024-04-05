package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ServerWebSocketHandler {
    final Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {

    }
}
