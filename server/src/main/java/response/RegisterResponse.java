package response;

import models.AuthData;

public record RegisterResponse(AuthData authData, int HTTPCode) {
}
