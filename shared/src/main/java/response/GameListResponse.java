package response;

import models.GameData;

import java.util.ArrayList;

public record GameListResponse(ArrayList<GameData> games) {
}
