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
    String webAddress = "http://localhost:";
    String port;
    URI uri;
    String baseUrl;
    String authToken = "";

    public ChessClient(String port) throws URISyntaxException {
        preLoginMenu();
        this.port = port;
        baseUrl = webAddress + port;
        uri = new URI(baseUrl);
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
                    register();
                    break;
                case "login":
                    login();
                    break;
                case "quit":
                    running = false;
                    break;
                case "help":
                    help(0);
                    break;
                default:
                    System.out.println("Please choose an option.");
            }
        }
        scanner.close();
    }

    private void postLoginMenu() {
        Scanner scanner = new Scanner(System.in);
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
                    logout();
                    running = false;
                    break;
                case "create":
                    createGame();
                    break;
                case "list":
                    listGames();
                    break;
                case "join":
                    joinGame();
                    break;
                case "watch":
                    watch();
                    break;
                case "help":
                    help(1);
                    break;
                default:
                    System.out.println("Please choose an option.");
            }
        }
        scanner.close();
    }

    private void watch() {
        Scanner scanner = new Scanner(System.in);
        int id = getId(scanner);

        //TODO: use id, empty color to join game as watcher
    }

    private void joinGame() {
        Scanner scanner = new Scanner(System.in);
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

    private void listGames() {
        //TODO: call the list games thing and get that printed pretty
    }

    private void createGame() {
        Scanner scanner = new Scanner(System.in);
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

        scanner.close();

        //TODO: using gameName, create a game in the server
    }

    private void logout() {
        //TODO: uses the existing authToken to logout
    }

    private void register() {
        Scanner scanner = new Scanner(System.in);
        String username = null;
        String password = null;
        String email = null;

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
        while (email == null) {
            System.out.println("Email:");
            System.out.print(">>>  ");
            email = scanner.nextLine();
            if (email.isEmpty()) {
                email = null;
            }
        }
        scanner.close();

        UserData userData = new UserData(username, password, email);
        String jsonData = gson.toJson(userData);

        try {
            ResponseRequest request = HttpConnection.startConnection(baseUrl + "/user", "POST", jsonData);

            if (request.statusCode() == 200) {
                authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
            } else {
                System.out.println(request.responseBody());
            }
        } catch(URISyntaxException | IOException e) {
            System.out.println("Register Error: " + e.getMessage());
        }
    }

    private void login() {
        Scanner scanner = new Scanner(System.in);
        boolean cont = true;
        String username;
        String password;

        while (cont) {
            System.out.println("Username: ");
            System.out.print(">>>  ");
            username = scanner.nextLine();
            if (username != null && !username.isEmpty()) {
                cont = false;
            }
        }
        cont = true;
        while (cont) {
            System.out.println("Password: ");
            System.out.print(">>>  ");
            password = scanner.nextLine();
            if (password != null && !password.isEmpty()) {
                cont = false;
            }
        }

        scanner.close();
        //TODO: use username, password, to login the user
    }

    private void help(int helpPage) {
        //TODO: print some useful help functionality
        if (helpPage == 0) {
            System.out.println("The stuff");
        } else if (helpPage == 1) {
            System.out.println("The stuff part 2");
        }
    }


}
