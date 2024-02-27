package server;

import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import response.*;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Handler {
    Gson gson = new Gson();
    public String registration(String userData) {
        UserData completeData = gson.fromJson(userData, UserData.class);
        RegisterResponse regResponse = UserService.createUser(completeData);
        return gson.toJson(regResponse);
    }

    public String login(String userData) {
        UserData completeData = gson.fromJson(userData, UserData.class);
        LoginResponse loginResponse = UserService.login(completeData);
        return gson.toJson(loginResponse);
    }

    public String logout(String header) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        LogoutResponse logoutResponse = AuthService.logout(authData);
        return gson.toJson(logoutResponse);
    }

    public String listGames(String header) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        ListGamesResponse listGamesResponse = GameService.listGames(authData);
        return gson.toJson(listGamesResponse);
    }

    public String createGame(String header, String newGameData) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        GameData gameData = gson.fromJson(newGameData, GameData.class);

        CreateGameResponse createGameResponse = GameService.createGame(gameData, authData);
        return gson.toJson(createGameResponse);
    }

    public String joinGame(String header, String newGameData) {
        AuthData authData = gson.fromJson(header, AuthData.class);
        GameData gameData = gson.fromJson(newGameData, GameData.class);

        JoinGameResponse joinGameResponse = GameService.joinGame(gameData, authData);
        return gson.toJson(joinGameResponse);
    }

    public String clearApp() {
        GameService.clearGames();
        UserService.clearData();
        AuthService.clearData();

        ClearResponse clearResponse = new ClearResponse(200);

        return gson.toJson(clearResponse);
    }
}
