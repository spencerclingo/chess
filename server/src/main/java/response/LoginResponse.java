package response;

import models.AuthData;

public record LoginResponse(AuthData authData, int HTTPCode) {
}
