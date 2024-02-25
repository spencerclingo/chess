package response;

public record RegisterResponse(String username, String authToken, String error400, String error403, String error500) {
}
