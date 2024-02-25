package response;

import models.GameData;

import java.util.ArrayList;

public record ListGamesResponse(ArrayList<GameData> listOfGames, int HTTPCode) {
}
