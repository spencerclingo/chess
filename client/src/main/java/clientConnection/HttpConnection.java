package clientConnection;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpConnection {
    public HttpConnection(String url, String method, String body) throws URISyntaxException, IOException {
        HttpURLConnection http = sendRequest(url, method, body);
        receiveResponse(http);
    }

    public static ResponseRequest startConnection(String url, String method, String body) throws IOException, URISyntaxException {
        HttpURLConnection http = sendRequest(url, method, body);
        return receiveResponse(http);
    }

    private static HttpURLConnection sendRequest(String url, String method, String body) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        writeRequestBody(body, http);
        http.connect();
        return http;
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static ResponseRequest receiveResponse(HttpURLConnection http) throws IOException {
        var statusCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();

        String responseBody = readResponseBody(http);
        //System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", statusCode, statusMessage, responseBody);
        return new ResponseRequest(statusCode, statusMessage, responseBody);
    }

    private static String readResponseBody(HttpURLConnection http) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        try (InputStream respBody = http.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(respBody));
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
        }
        return responseBody.toString();
    }


}
