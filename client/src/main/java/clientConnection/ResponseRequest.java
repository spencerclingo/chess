package clientConnection;

public record ResponseRequest(int statusCode, String statusMessage, Object responseBody) {
}
