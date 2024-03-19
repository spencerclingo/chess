package clientConnection;

import com.google.gson.Gson;
import models.AuthData;
import models.UserData;
import response.JoinGameRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ChessClient {

    Gson gson = new Gson();
    int port;
    URI uri;
    String baseUrl;
    String authToken = "";

    public ChessClient(int port) throws URISyntaxException {
        this.port = port;
        baseUrl = "http://localhost:" + port + "/";
        uri = new URI(baseUrl);
        preLoginMenu();
    }

    private void preLoginMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to my Chess Server! Type help if you need help!");
            System.out.println("\t register - to create an account");
            System.out.println("\t login    - to you existing account and play");
            System.out.println("\t quit     - exits the program");
            System.out.println("\t help     - print more helpful instructions");

            System.out.print(">>>");

            String choice = scanner.nextLine().toLowerCase();
            switch (choice) {
                case "register":
                    register(scanner);
                    postLoginMenu(scanner);
                    break;
                case "login":
                    login(scanner);
                    postLoginMenu(scanner);
                    break;
                case "quit":
                    running = false;
                    break;
                case "help":
                    help(scanner, 0);
                    break;
                case "clear":
                    clear();
                    break;
                default:
                    System.out.println("Please choose an option.");
            }
        }
        System.out.println();
        scanner.close();
    }

    private void postLoginMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("Welcome to my Chess Server! Type help if you need help!");
            System.out.println("\t logout   - logout from the server");
            System.out.println("\t create   - a new chess game");
            System.out.println("\t list     - lists all games");
            System.out.println("\t join     - join an existing game by ID");
            System.out.println("\t watch    - watch an existing game");
            System.out.println("\t help     - print more helpful instructions");

            System.out.print(">>>  ");

            String choice = scanner.nextLine().toLowerCase();
            switch (choice) {
                case "logout":
                    logout(scanner);
                    running = false;
                    break;
                case "create":
                    createGame(scanner);
                    break;
                case "list":
                    listGames(scanner);
                    break;
                case "join":
                    joinGame(scanner);
                    break;
                case "watch":
                    watch(scanner);
                    break;
                case "help":
                    help(scanner, 1);
                    break;
                default:
                    System.out.println("Please choose an option.");
            }
        }
    }

    private void clear() {
        try {
            ResponseRequest request = HttpConnection.startConnection(baseUrl + "/db", "DELETE", "");

            if (request.statusCode() != 200) {
                System.out.println("CLEAR DATABASE FAILED");
            }
        } catch(IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void watch(Scanner scanner) {
        int id = getId(scanner);

        //TODO: use id, empty color to join game as watcher
    }

    private void joinGame(Scanner scanner) {
        int id = getId(scanner);

        System.out.println("Choose color you want to join (or none if you want to observe)");
        System.out.println("white or black: ");
        System.out.print(">>>  ");

        String color = scanner.nextLine();

        String jsonString = gson.toJson(new JoinGameRequest(color, id));

        //TODO: use id, color to join a game
    }

    private int getId(Scanner scanner) {
        System.out.println("Game ID: ");
        System.out.print(">>>  ");

        return scanner.nextInt();
    }

    private void listGames(Scanner scanner) {
        //TODO: call the list games thing and get that printed pretty
    }

    private void createGame(Scanner scanner) {
        boolean cont = true;
        String gameName;

        while (cont) {
            System.out.println("Name for game: ");
            System.out.print(">>>  ");
            gameName = scanner.nextLine();
            if (gameName != null && !gameName.isEmpty()) {
                cont = false;
            }
        }

        //TODO: using gameName, create a game in the server
    }

    private void logout(Scanner scanner) {
        //TODO: uses the existing authToken to logout
    }

    private void register(Scanner scanner) {
        String[] userInfo = getUserInfo(scanner);
        String username = userInfo[0];
        String password = userInfo[1];
        String email = null;

        while (email == null) {
            System.out.println("Email:");
            System.out.print(">>>  ");
            email = scanner.nextLine();
            if (email.isEmpty()) {
                email = null;
            }
        }

        UserData userData = new UserData(username, password, email);
        String jsonData = gson.toJson(userData);

        try {
            ResponseRequest request = HttpConnection.startConnection(baseUrl + "/user", "POST", jsonData);

            if (request.statusCode() == 200) {
                authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
            } else {
                System.out.println("Error code: " + request.statusCode());
                System.out.println(request.responseBody());
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void login(Scanner scanner) {
        String[] userInfo = getUserInfo(scanner);
        String username = userInfo[0];
        String password = userInfo[1];

        UserData userData = new UserData(username, password, null);
        String jsonData = gson.toJson(userData);

        try {
            ResponseRequest request = HttpConnection.startConnection(baseUrl + "/session", "POST", jsonData);

            if (request.statusCode() == 200) {
                authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
            } else {
                System.out.println();
                System.out.println(request.responseBody());
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] getUserInfo(Scanner scanner) {
        String[] userInfo = new String[2];

        String username = null;
        String password = null;

        while (username == null) {
            System.out.println("Username: ");
            System.out.print(">>>  ");
            username = scanner.nextLine();
            if (username.isEmpty()) {
                username = null;
            }
        }
        while (password == null) {
            System.out.println("Password: ");
            System.out.print(">>>  ");
            password = scanner.nextLine();
            if (password.isEmpty()) {
                password = null;
            }
        }

        userInfo[0] = username;
        userInfo[1] = password;
        return userInfo;
    }

    private void help(Scanner scanner, int helpPage) {
        //TODO: print some useful help functionality
        if (helpPage == 0) {
            System.out.println("The stuff");
        } else if (helpPage == 1) {
            System.out.println("The stuff part 2");
        }
    }


}
