package response;

import models.AuthData;
import models.UserData;

public record LoginResponse(AuthData authData, int HTTPCode) {
}
