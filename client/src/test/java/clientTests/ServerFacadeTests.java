package clientTests;

import clientConnection.ResponseRequest;
import clientConnection.ServerFacade;
import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import org.junit.jupiter.api.*;
import response.GameIDResponse;
import server.Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String url = "http://localhost:";
    private static int port = 0;
    Gson gson = new Gson();

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
        server.stop();
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
                System.out.println();
                System.out.println(request.responseBody());
                fail("An error should have been thrown");
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
            assertEquals("Server returned HTTP response code: 401 for URL: http://localhost:" + port + "//session", e.getMessage());
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
    public void createBadNewGame() throws IOException, URISyntaxException {
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
                fail("Game was not created, but error should have been thrown");
            }
        } catch(Exception e) {
            assertTrue(true, "Exception thrown, like it should be");
        }
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
                fail("Failed to logout, which would be good but an exception should've been thrown");
            } else {
                fail("Logged out successfully, despite there being nothing to logout");
            }
        } catch(URISyntaxException | IOException e) {
            assertTrue(true,"Exception thrown");
        }
    }

    @Test
    public void getGameEmptyList() {
        assertTrue(true);
    }
}
