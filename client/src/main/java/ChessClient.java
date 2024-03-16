import java.util.Scanner;

public class ChessClient {
    public ChessClient() {
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

        //TODO: using gameName, create a game in the server
    }

    private void logout() {
        //TODO: uses the existing authToken to logout
    }

    private void register() {
        Scanner scanner = new Scanner(System.in);
        boolean cont = true;
        String username;
        String password;
        String email;

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
        cont = true;
        while (cont) {
            System.out.println("Email:");
            System.out.print(">>>  ");
            email = scanner.nextLine();
            if (email != null && !email.isEmpty()) {
                cont = false;
            }
        }
        //TODO: User the username, password, email, in the connection to the server
        scanner.close();
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
        //TODO: use username, password, to login the user
    }

    private void help(int helpPage) {
        //TODO: print some useful help functionality
    }


}
