package clientTests;

import clientConnection.ResponseRequest;
import clientConnection.ServerFacade;
import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import org.junit.jupiter.api.*;
import response.GameIDResponse;
import response.GameListResponse;
import response.JoinGameRequest;
import server.Server;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String url = "http://localhost:";
    private static int port = 0;
    final Gson gson = new Gson();

    @BeforeAll
    public static void init() {
        server = new Server();
        ServerFacadeTests.port = server.run(ServerFacadeTests.port);
        System.out.println("Started test HTTP server on " + port);
        url = url + port + "/";
    }

    @BeforeEach
    public void startUp() {
        //Clear at startup of each test
        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/db", "DELETE", "", "");

            if (request.statusCode() != 200) {
                System.out.println("CLEAR DATABASE FAILED");
            } else {
                System.out.println("Clear Database successful");
            }
        } catch(IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/db", "DELETE", "", "");

            if (request.statusCode() != 200) {
                System.out.println("CLEAR DATABASE FAILED");
            } else {
                System.out.println("Clear Database successful");
            }
        } catch(IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        server.stop();
    }

    private String registerUser() throws IOException, URISyntaxException {
        String username = "username";
        String password = "password";
        String email = "email";
        String authToken = null;

        String jsonData = gson.toJson(new UserData(username, password, email));

        ResponseRequest request = ServerFacade.startConnection(url + "/user", "POST", jsonData, "");

        if (request.statusCode() == 200) {
            return gson.fromJson(request.responseBody(), AuthData.class).authToken();
        } else {
            fail("Registering failed when it shouldn't");
            return authToken;
        }
    }

    private int createGame(String authToken) throws IOException, URISyntaxException {
        int gameID = -1;
        GameData gameData = new GameData(gameID, null, null, "newGame", null);
        String jsonData = gson.toJson(gameData);

        ResponseRequest responseRequest = ServerFacade.startConnection(url + "/game", "POST", jsonData, authToken);

        if (responseRequest.statusCode() == 200) {
            gameID = gson.fromJson(responseRequest.responseBody(), GameIDResponse.class).gameID();
            return gameID;
        } else {
            throw new RuntimeException();
        }
    }


    @Test
    public void registerTest() {
        String username = "username";
        String password = "password";
        String email = "email";
        String authToken = null;


        UserData userData = new UserData(username, password, email);
        String jsonData = gson.toJson(userData);

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/user", "POST", jsonData, "");

            if (request.statusCode() == 200) {
                authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
            } else {
                System.out.println("Error code: " + request.statusCode());
                System.out.println(request.responseBody());
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
        }

        Assertions.assertNotNull(authToken);
    }

    @Test
    public void registerExistingUserTest() {
        String username = "username";
        String password = "password";
        String email = "email";
        String authToken = null;


        UserData userData = new UserData(username, password, email);
        String jsonData = gson.toJson(userData);

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/user", "POST", jsonData, "");

            if (request.statusCode() == 200) {
                assertTrue(true);
            } else {
                System.out.println("Error code: " + request.statusCode());
                System.out.println(request.responseBody());
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
            fail();
        }

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/user", "POST", jsonData, "");

            if (request.statusCode() == 200) {
                authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
                fail();
            } else {
                System.out.println("Error code: " + request.statusCode());
                System.out.println(request.responseBody());
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
            assertEquals("Server returned HTTP response code: 403 for URL: http://localhost:" + port + "//user", e.getMessage());
        }
        Assertions.assertNull(authToken);
    }

    @Test
    public void loginValidUser() throws IOException, URISyntaxException {
        String username = "username";
        String password = "password";
        String email = "email";
        String authToken = null;

        UserData userData = new UserData(username, password, email);
        String jsonData = gson.toJson(userData);

        ServerFacade.startConnection(url + "/user", "POST", jsonData, "");

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/session", "POST", jsonData, authToken);

            if (request.statusCode() == 200) {
                authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
                assertTrue(true, "This means you login properly");
            } else {
                System.out.println();
                System.out.println(request.responseBody());
                fail("You didn't login properly");
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
            fail("An error was thrown where there should not be");
        }

        assertNotNull(authToken);
    }

    @Test
    public void loginInvalidUser() {
        String username = "username";
        String password = "password";
        String email = "email";
        String authToken = null;


        UserData userData = new UserData(username, password, email);
        String jsonData = gson.toJson(userData);

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/session", "POST", jsonData, authToken);

            if (request.statusCode() == 200) {
                authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
                fail("This means you login properly, yet no user is registered with that username");
            } else {
                assertEquals("", request.responseBody());
            }
        } catch(URISyntaxException | IOException e) {
            fail("Exception was thrown where none should be");
        }

        assertNull(authToken);
    }

    @Test
    public void createNewGame() throws IOException, URISyntaxException {
        String username = "username";
        String password = "password";
        String email = "email";
        String authToken = null;
        String gameName = "newGame";
        int gameID = -1;


        UserData userData = new UserData(username, password, email);
        String jsonData = gson.toJson(userData);

        ResponseRequest request = ServerFacade.startConnection(url + "/user", "POST", jsonData, "");

        if (request.statusCode() == 200) {
            authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
        } else {
            fail("Registering failed when it shouldn't");
        }

        GameData gameData = new GameData(gameID, null, null, gameName, null);
        jsonData = gson.toJson(gameData);

        ResponseRequest responseRequest = ServerFacade.startConnection(url + "/game", "POST", jsonData, authToken);

        if (responseRequest.statusCode() == 200) {
            gameID = gson.fromJson(responseRequest.responseBody(), GameIDResponse.class).gameID();
            assertEquals(1, gameID);
        } else {
            fail("Game was not created");
        }
        assertNotNull(authToken);
    }

    @Test
    public void createBadNewGame() {
        String authToken = null;
        String gameName = "newGame";
        int gameID = -1;

        GameData gameData = new GameData(gameID, null, null, gameName, null);
        String jsonData = gson.toJson(gameData);

        try {
            ResponseRequest responseRequest = ServerFacade.startConnection(url + "/game", "POST", jsonData, authToken);

            if (responseRequest.statusCode() == 200) {
                gameID = gson.fromJson(responseRequest.responseBody(), GameIDResponse.class).gameID();
            } else {
                assertTrue(true, "Game was not created!");
            }
        } catch(Exception e) {
            fail("Error was thrown when none should be");
        }
        assertEquals(-1, gameID);
    }

    @Test
    public void logoutValidUser() throws IOException, URISyntaxException {
        String username = "username";
        String password = "password";
        String email = "email";
        String authToken = null;

        String jsonData = gson.toJson(new UserData(username, password, email));

        ResponseRequest request = ServerFacade.startConnection(url + "/user", "POST", jsonData, "");

        if (request.statusCode() == 200) {
            authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
        } else {
            fail("Registering failed when it shouldn't");
        }

        try {
            request = ServerFacade.startConnection(url + "/session", "DELETE", "", authToken);

            if (request.statusCode() != 200) {
                fail("Failed to logout");
            } else {
                assertTrue(true, "Logged out successfully");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Exception thrown");
        }
    }

    @Test
    public void logoutInvalidUser() {
        String authToken = "";

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/session", "DELETE", "", authToken);

            if (request.statusCode() != 200) {
                assertTrue(true,"Failed to logout");
            } else {
                fail("Logged out successfully, despite there being nothing to logout");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Exception was thrown where none should be");
        }
    }

    @Test
    public void getGameEmptyList() {
        String authToken = null;
        try {
            authToken = registerUser();
        } catch(IOException | URISyntaxException e) {
            fail("Register failed");
        }

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/game", "GET", "", authToken);

            if (request.statusCode() == 200) {
                GameListResponse gameList = gson.fromJson(request.responseBody(), GameListResponse.class);
                assertEquals(0, gameList.games().size());
            } else {
                fail("Status code should be 200");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Error thrown when there should be none");
        }
    }

    @Test
    public void getGameOneInList() {
        String authToken = null;
        try {
            authToken = registerUser();
            createGame(authToken);
        } catch(IOException | URISyntaxException e) {
            fail("Register or game creation failed");
        }

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/game", "GET", "", authToken);

            if (request.statusCode() == 200) {
                GameListResponse gameList = gson.fromJson(request.responseBody(), GameListResponse.class);
                assertEquals(1, gameList.games().size());
            } else {
                fail("Status code should be 200");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Error thrown when there should be none");
        }
    }

    @Test
    public void getGameManyInList() {
        String authToken = null;
        try {
            authToken = registerUser();
            createGame(authToken);
            createGame(authToken);
            createGame(authToken);
            createGame(authToken);
            createGame(authToken);
            createGame(authToken);
            createGame(authToken);
        } catch(IOException | URISyntaxException e) {
            fail("Register or game creation failed");
        }

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/game", "GET", "", authToken);

            if (request.statusCode() == 200) {
                GameListResponse gameList = gson.fromJson(request.responseBody(), GameListResponse.class);
                assertEquals(7, gameList.games().size());
            } else {
                fail("Status code should be 200");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Error thrown when there should be none");
        }
    }

    @Test
    public void joinValidGame() {
        String authToken = null;
        int gameID = -1;
        try {
            authToken = registerUser();
            gameID = createGame(authToken);
        } catch(IOException | URISyntaxException e) {
            fail("Register or game creation failed");
        }

        String jsonString = gson.toJson(new JoinGameRequest("white", gameID));

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/game", "PUT", jsonString, authToken);

            if (request.statusCode() == 200) {
                assertTrue(true, "Status code 200 means success!");
            } else {
                fail("Status code should be 200");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Error thrown when there should be none");
        }
    }

    @Test
    public void joinInvalidGame() {
        String authToken = null;
        int gameID = -1;
        try {
            authToken = registerUser();
        } catch(IOException | URISyntaxException e) {
            fail("Register failed");
        }

        String jsonString = gson.toJson(new JoinGameRequest("white", gameID));

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/game", "PUT", jsonString, authToken);

            if (request.statusCode() == 200) {
                fail("Game does not exist, no game should have been joined");
            } else {
                assertTrue(true,"Failed to join game that didn't exist");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Error thrown when there should be none");
        }
    }

    @Test
    public void watchValidGame() {
        String authToken = null;
        int gameID = -1;
        try {
            authToken = registerUser();
            gameID = createGame(authToken);
        } catch(IOException | URISyntaxException e) {
            fail("Register or game creation failed");
        }

        String jsonString = gson.toJson(new JoinGameRequest("empty", gameID));

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/game", "PUT", jsonString, authToken);

            if (request.statusCode() == 200) {
                assertTrue(true, "Status code 200 means success!");
            } else {
                fail("Status code should be 200");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Error thrown when there should be none");
        }
    }

    @Test
    public void watchInvalidGame() {
        String authToken = null;
        int gameID = -1;
        try {
            authToken = registerUser();
        } catch(IOException | URISyntaxException e) {
            fail("Register failed");
        }

        String jsonString = gson.toJson(new JoinGameRequest("", gameID));

        try {
            ResponseRequest request = ServerFacade.startConnection(url + "/game", "PUT", jsonString, authToken);

            if (request.statusCode() == 200) {
                fail("Game does not exist, no game should have been joined");
            } else {
                assertTrue(true,"Failed to join game that didn't exist");
            }
        } catch(URISyntaxException | IOException e) {
            fail("Error thrown when there should be none");
        }
    }
}
