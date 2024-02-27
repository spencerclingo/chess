package server;

import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import spark.*;
import service.*;
import response.*;

import java.util.ArrayList;

public class Server {

    Gson gson = new Gson();
    String emptyJson = "{}";

    public int run(int desiredPort) {
        ServiceInitializer.initialize();

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object getResponseBody(Response response, int HTTPCode, AuthData authData) {
        response.status(HTTPCode);

        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            return gson.toJson(authData);
        } else {
            if (find500Error(HTTPCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object getResponseBody(Response response, int HTTPCode) {
        response.status(HTTPCode);
        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            return emptyJson;
        } else {
            if (find500Error(HTTPCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object clearDatabase(Request request, Response response) {
        GameService.clearGames();
        UserService.clearData();
        AuthService.clearData();

        ClearResponse clearResponse = new ClearResponse(200);
        return getResponseBody(response, clearResponse.HTTPCode());
    }

    private Object joinGame(Request request, Response response) {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        GameData gameData = gson.fromJson(request.body(), GameData.class);

        JoinGameResponse joinGameResponse = GameService.joinGame(gameData, authData);
        return getResponseBody(response, joinGameResponse.HTTPCode());
    }

    private Object createGame(Request request, Response response) {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        GameData gameData = gson.fromJson(request.body(), GameData.class);

        CreateGameResponse createGameResponse = GameService.createGame(gameData, authData);
        int HTTPCode = createGameResponse.HTTPCode();

        response.status(HTTPCode);
        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            GameIDResponse gameIDResponse = new GameIDResponse(createGameResponse.gameID());
            return gson.toJson(gameIDResponse);
        } else {
            if (find500Error(HTTPCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object listGames(Request request, Response response) {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        ListGamesResponse listGamesResponse = GameService.listGames(authData);

        int HTTPCode = listGamesResponse.HTTPCode();
        response.status(HTTPCode);

        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            GameListResponse gameList = new GameListResponse(listGamesResponse.listOfGames());
            return gson.toJson(gameList);
        } else {
            if (find500Error(HTTPCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object logoutUser(Request request, Response response) {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        LogoutResponse logoutResponse = AuthService.logout(authData);

        return getResponseBody(response, logoutResponse.HTTPCode());
    }

    private Object loginUser(Request request, Response response) {
        UserData userData = gson.fromJson(request.body(), UserData.class);
        LoginResponse loginResponse = UserService.login(userData);

        return getResponseBody(response, loginResponse.HTTPCode(), loginResponse.authData());
    }

    private Object registerUser(Request request, Response response) {
        UserData userData = gson.fromJson(request.body(), UserData.class);
        RegisterResponse registerResponse = UserService.createUser(userData);

        return getResponseBody(response, registerResponse.HTTPCode(), registerResponse.authData());
    }

    private String switchCases(int HTTPCode) {
        switch (HTTPCode) {
            case (200) -> {
                return "{}";
            }
            case (400) -> {
                return "{ \"message\": \"Error: bad request\" }";
            }
            case (401) -> {
                return "{ \"message\": \"Error: unauthorized\" }";
            }
            case (403) -> {
                return "{ \"message\": \"Error: already taken\" }";
            }
            default -> {
                return "{ \"message\": \"Error: description\" }";
            }
        }
    }

    private boolean find500Error(int HTTPCode) {
        return switch (HTTPCode) {
            case (200), (400), (401), (403) -> false;
            default -> true;
        };
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
