package clientConnection;

public record ResponseRequest(int statusCode, String statusMessage, String responseBody) {
}
