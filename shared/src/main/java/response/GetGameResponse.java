package response;

import models.GameData;

public record GetGameResponse(GameData gameData, String authToken, int httpCode) {
}
