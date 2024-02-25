package server;

import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

public class Handler {
    Gson gson = new Gson();
    public String registration(String userData) {
        UserData completeData = gson.fromJson(userData, UserData.class);
        AuthData authData = UserService.createUser(completeData);
        return gson.toJson(authData);
    }

    public String login(String userData) {
        UserData completeData = gson.fromJson(userData, UserData.class);
        AuthData authData = UserService.login(completeData);
        return gson.toJson(authData);
    }

    public String logout(String header) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        boolean success = AuthService.logout(authData);
        return gson.toJson(success);
    }

    public String listGames(String header) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        ArrayList<GameData> listOfGames = GameService.listGames(authData);
        return gson.toJson(listOfGames);
    }

    public String createGame(String header, String newGameData) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        GameData gameData = gson.fromJson(newGameData, GameData.class);

        int gameID = GameService.createGame(gameData, authData);
        return gson.toJson(gameID);
    }

    public String joinGame(String header, String newGameData) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        GameData gameData = gson.fromJson(newGameData, GameData.class);

        boolean success = GameService.joinGame(gameData, authData);
        return gson.toJson(success);
    }

    public String clearApp() {
        GameService.clearGames();
        UserService.clearData();
        AuthService.clearData();

        return gson.toJson(true);
    }
}
