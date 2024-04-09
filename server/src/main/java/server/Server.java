package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import spark.*;
import service.*;
import response.*;
import dataAccess.*;
import models.*;

public class Server {

    final Gson gson = new Gson();
    final String emptyJson = "{}";

    public int run(int desiredPort) {
        try {
            ServiceInitializer.initialize();
        } catch (DataAccessException dae) {
            System.out.println("Database could not be initialized");
            return -999;
        }

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", new ServerWebSocketHandler());

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

    private Object getResponseBody(Response response, int httpCode, AuthData authData) {
        response.status(httpCode);
        String error = switchCases(httpCode);

        if (error.equals(emptyJson)) {
            return gson.toJson(authData);
        } else {
            if (find500Error(httpCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object getResponseBody(Response response, int httpCode) {
        response.status(httpCode);
        String error = switchCases(httpCode);

        if (error.equals(emptyJson)) {
            return emptyJson;
        } else {
            if (find500Error(httpCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object clearDatabase(Request request, Response response) {
        ClearResponse clearResponse;
        if (GameService.clearGames() && UserService.clearData() && AuthService.clearData()) {
            clearResponse = new ClearResponse(200);
        } else {
            clearResponse = new ClearResponse(500);
        }
        return getResponseBody(response, clearResponse.httpCode());
    }

    private Object joinGame(Request request, Response response) throws DataAccessException {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);

        JoinGameResponse joinGameResponse = GameService.joinGame(joinGameRequest, authData);
        return getResponseBody(response, joinGameResponse.httpCode());
    }

    private Object createGame(Request request, Response response) {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        GameData gameData = gson.fromJson(request.body(), GameData.class);

        CreateGameResponse createGameResponse = GameService.createGame(gameData, authData);
        int httpCode = createGameResponse.httpCode();

        response.status(httpCode);
        String error = switchCases(httpCode);

        if (error.equals(emptyJson)) {
            GameIDResponse gameIDResponse = new GameIDResponse(createGameResponse.gameID());
            return gson.toJson(gameIDResponse);
        } else {
            if (find500Error(httpCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object listGames(Request request, Response response) {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        ListGamesResponse listGamesResponse = GameService.listGames(authData);

        int httpCode = listGamesResponse.httpCode();
        response.status(httpCode);

        String error = switchCases(httpCode);

        if (error.equals(emptyJson)) {
            GameListResponse gameList = new GameListResponse(listGamesResponse.listOfGames());
            return gson.toJson(gameList);
        } else {
            if (find500Error(httpCode)) {
                response.status(500);
            }
            return error;
        }
    }

    private Object logoutUser(Request request, Response response) {
        AuthData authData = new AuthData(request.headers("Authorization"), null);
        LogoutResponse logoutResponse = AuthService.logout(authData);

        return getResponseBody(response, logoutResponse.httpCode());
    }

    private Object loginUser(Request request, Response response) {
        UserData userData = gson.fromJson(request.body(), UserData.class);
        LoginResponse loginResponse = UserService.login(userData);

        return getResponseBody(response, loginResponse.httpCode(), loginResponse.authData());
    }

    private Object registerUser(Request request, Response response) {
        UserData userData = gson.fromJson(request.body(), UserData.class);
        try {
            RegisterResponse registerResponse = UserService.createUser(userData);
            return getResponseBody(response, registerResponse.httpCode(), registerResponse.authData());
        } catch(DataAccessException dae) {
            return getResponseBody(response, 500, null);
        }

    }

    private String switchCases(int httpCode) {
        switch (httpCode) {
            case (200), (201) -> {
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

    private boolean find500Error(int httpCode) {
        return switch (httpCode) {
            case (200), (201), (400), (401), (403) -> false;
            default -> true;
        };
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
