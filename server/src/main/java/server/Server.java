package server;

import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import spark.*;
import dataAccess.*;
import service.*;
import response.*;

public class Server {

    private final Handler handler = new Handler();
    Gson gson = new Gson();
    String emptyJson = "{}";

    public int run(int desiredPort) {
        ServiceInitializer servInit = new ServiceInitializer();

        Spark.port(desiredPort);

        Spark.staticFiles.location("../main/resources/web");

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

    private Object clearDatabase(Request request, Response response) {
        GameService.clearGames();
        UserService.clearData();
        AuthService.clearData();

        ClearResponse clearResponse = new ClearResponse(200);
        return getResponseBody(response, clearResponse.HTTPCode());
    }

    private Object joinGame(Request request, Response response) {
        AuthData authData = gson.fromJson(request.headers("Authorization"), AuthData.class);
        GameData gameData = gson.fromJson(request.body(), GameData.class);

        JoinGameResponse joinGameResponse = GameService.joinGame(gameData, authData);
        return getResponseBody(response, joinGameResponse.HTTPCode());
    }

    private Object getResponseBody(Response response, int HTTPCode) {

        response.status(HTTPCode);
        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            response.body(emptyJson);
        } else {
            response.body(gson.toJson(error));
        }

        return "";
    }

    private Object createGame(Request request, Response response) {
        AuthData authData = gson.fromJson(request.headers("Authorization"), AuthData.class);
        GameData gameData = gson.fromJson(request.body(), GameData.class);

        CreateGameResponse createGameResponse = GameService.createGame(gameData, authData);
        int HTTPCode = createGameResponse.HTTPCode();

        response.status(HTTPCode);
        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            response.body(gson.toJson(createGameResponse.gameID()));
        } else {
            response.body(gson.toJson(error));
        }

        return "";
    }

    private Object listGames(Request request, Response response) {
        AuthData authData = gson.fromJson(request.headers("Authorization"), AuthData.class);
        ListGamesResponse listGamesResponse = GameService.listGames(authData);

        int HTTPCode = listGamesResponse.HTTPCode();
        response.status(HTTPCode);

        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            response.body(gson.toJson(listGamesResponse.listOfGames()));
        } else {
            response.body(gson.toJson(error));
        }

        return "";
    }

    private Object logoutUser(Request request, Response response) {
        AuthData authData = gson.fromJson(request.headers("Authorization"), AuthData.class);
        LogoutResponse logoutResponse = AuthService.logout(authData);

        return getResponseBody(response, logoutResponse.HTTPCode());
    }

    private Object loginUser(Request request, Response response) {
        UserData userData = gson.fromJson(request.headers("Authorization"), UserData.class);
        LoginResponse loginResponse = UserService.login(userData);

        return getResponseBody(response, loginResponse.HTTPCode(), loginResponse.authData());
    }

    private Object registerUser(Request request, Response response) {
        UserData userData = gson.fromJson(request.headers("Authorization"), UserData.class);
        RegisterResponse registerResponse = UserService.createUser(userData);

        return getResponseBody(response, registerResponse.HTTPCode(), registerResponse.authData());
    }

    private Object getResponseBody(Response response, int HTTPCode, AuthData authData) {
        response.status(HTTPCode);

        String error = switchCases(HTTPCode);

        if (error.equals(emptyJson)) {
            response.body(gson.toJson(authData));
        } else {
            response.body(gson.toJson(error));
        }

        return "";
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

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
