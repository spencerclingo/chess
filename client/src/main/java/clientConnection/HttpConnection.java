package clientConnection;

import java.io.IOException;
import java.net.URISyntaxException;

public class HttpConnection {

    private static String baseUrl;

    public static ResponseRequest getRequest(String path, String method, String body, String authToken) {
        try {
            return ServerFacade.startConnection(baseUrl + path, method, body, authToken);
        } catch(IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setBaseUrl(String baseUrl) {
        HttpConnection.baseUrl = baseUrl;
    }

}
