package clientTests;

import clientConnection.ResponseRequest;
import clientConnection.ServerFacade;
import com.google.gson.Gson;
import models.AuthData;
import models.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ServerFacadeTests {

    private static Server server;
    private static String url = "http://localhost:";
    private static int port = 0;
    Scanner scannerMock = mock(Scanner.class);
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
                //gson.fromJson(request.responseBody(), AuthData.class);
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

}
