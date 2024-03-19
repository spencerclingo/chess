package clientTests;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static String url = "http://localhost:";
    private static int port = 0;

    @BeforeAll
    public static void init() {
        server = new Server();
        ServerFacadeTests.port = server.run(ServerFacadeTests.port);
        System.out.println("Started test HTTP server on " + port);
        url = url + port + "/";
    }

    @BeforeEach
    public void startUp() {

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
