package clientConnection;

import chess.*;
import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import response.GameIDResponse;
import response.GameListResponse;
import response.JoinGameRequest;
import ui.ChessBoardPicture;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientMenu {

    final Gson gson = new Gson();
    final String baseUrl;
    String authToken = "";
    static String savedUsername = "";
    final String clearPassword = "clear";
    final int port;
    ClientWebSocketHandler webSocket;
    private static ChessGame game;
    private static String color;
    final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public ClientMenu(int port) {
        this.port = port;
        baseUrl = "http://localhost:" + port + "/";
        preLoginMenu();
    }

    private void setCommandLine() {
        out.print(SET_TEXT_NOT_BOLD);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void preLoginMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean help = true;
        HttpConnection.setBaseUrl(baseUrl);

        while (true) {
            setCommandLine();
            if (help) {
                System.out.println("Welcome to my Chess Server! Type help if you need help!");
                System.out.println("\t register - to create an account");
                System.out.println("\t login    - to your existing account and play");
                System.out.println("\t quit     - exits the program");
                System.out.println("\t help     - print more helpful instructions");
            }
            help = true;

            System.out.print(">>>  ");

            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("help")) {
                help(0);
                help = false;
                continue;
            }

            switch (choice) {
                case "register":
                    register(scanner);
                    if (authToken != null) {
                        if (!authToken.isEmpty()) {
                            postLoginMenu(scanner);
                        } else {
                            System.out.println("Cannot register user. Username already taken.");
                        }
                    } else {
                        System.out.println("Cannot register user. Username already taken.");
                    }
                    break;
                case "login":
                    login(scanner);
                    if (authToken != null) {
                        if (!authToken.isEmpty()) {
                            postLoginMenu(scanner);
                        } else {
                            System.out.println("Cannot login user.");
                        }
                    } else {
                        System.out.println("Cannot login user.");
                    }
                    break;
                case "quit":
                    System.out.println();
                    scanner.close();
                    return;
                case "clear":
                    clear(scanner);
                    break;
                default:
                    System.out.println("Please choose an option.");
            }
        }
    }

    private void postLoginMenu(Scanner scanner) {
        boolean help = true;

        while (true) {
            setCommandLine();
            if (help) {
                System.out.println("Welcome to my Chess Server! Type help if you need help!");
                System.out.println("\t logout   - logout from the server");
                System.out.println("\t create   - a new chess game");
                System.out.println("\t list     - lists all games");
                System.out.println("\t join     - join an existing game by ID");
                System.out.println("\t watch    - watch an existing game");
                System.out.println("\t help     - print more helpful instructions");
            }
            help = true;

            System.out.print(">>>  ");

            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("help")) {
                help(1);
                help = false;
                continue;
            }

            switch (choice) {
                case "logout":
                    logout();
                    return;
                case "create":
                    createGame(scanner);
                    break;
                case "list":
                    listGames();
                    break;
                case "join":
                    joinGame(scanner);
                    break;
                case "watch":
                    watch(scanner);
                    break;
                default:
                    System.out.println("Please choose an option.");
            }
        }
    }

    private void inGameMenu(Scanner scanner) {
        boolean help = false;
        System.out.println("For a list of commands and their purpose, type 'help'");

        while (true) {
            setCommandLine();
            if (help) {
                System.out.println("Here are your in-game commands. Type help if you need help!");
                System.out.println("\t redraw    - redraws the current chess board");
                System.out.println("\t make move - move one of your pieces");
                System.out.println("\t highlight - highlight moves a piece can make");
                System.out.println("\t resign    - concede to your opponent");
                System.out.println("\t leave     - leave the game, return to logged in menu");
                System.out.println("\t help      - gives a deeper explanation of each option");
            }
            help = true;

            System.out.print(">>>  ");

            String choice = scanner.nextLine().toLowerCase();

            if (choice.equals("help")) {
                help(2);
                help = false;
                continue;
            }

            switch (choice) {
                case "redraw":
                    redraw();
                    break;
                case "make move":
                    movePiece(scanner);
                    //Sends a make move message, pass in start position and end position. Use dict for letter to number calculations
                    break;
                case "highlight":
                    highlight(scanner);
                    //Passes a set of valid moves to ChessBoardPicture, highlights squares
                    break;
                case "resign":
                    resign();
                    break;
                case "leave":
                    leave();
                    return;
            }
        }
    }

    private void clear(Scanner scanner) {
        String password = scanner.nextLine().toLowerCase();

        if (!password.equals(clearPassword)) {
            return;
        }

        ResponseRequest request = HttpConnection.getRequest("/db", "DELETE", "", authToken);

        if (request.statusCode() != 200) {
            System.out.println("CLEAR DATABASE FAILED");
            printErrorMessages(request.statusCode());
        } else {
            authToken = "";
            System.out.println("Clear Database successful (good job remembering the password)");
        }
    }

    private void watch(Scanner scanner) {
        int id = getId(scanner);

        String jsonString = gson.toJson(new JoinGameRequest(null, id));

        joinGameHttp(jsonString, id, false, null, scanner);
    }

    private void joinGame(Scanner scanner) {
        int id = getId(scanner);

        System.out.println("Choose color you want to join (or none if you want to observe)");
        System.out.println("white or black: ");
        System.out.print(">>>  ");

        String color = scanner.nextLine();

        String jsonString = gson.toJson(new JoinGameRequest(color, id));

        joinGameHttp(jsonString, id, true, color, scanner);
    }

    private void joinGameHttp(String jsonString, int gameID, boolean player, String color, Scanner scanner) {
        try {
            webSocket = new ClientWebSocketHandler(baseUrl);

            ResponseRequest request = HttpConnection.getRequest("/game", "PUT", jsonString, authToken);

            if (request.statusCode() == 201) {
                System.out.println("Already joined as that color!");
            } else if (request.statusCode() != 200) {
                printErrorMessages(request.statusCode());
                return;
            }

            ClientMenu.color = color;

            System.out.println(gameID);

            UserGameCommand userGameCommand;
            if (player) {
                userGameCommand = new UserGameCommand(authToken, UserGameCommand.CommandType.JOIN_PLAYER, gameID, savedUsername, null);
            } else {
                userGameCommand = new UserGameCommand(authToken, UserGameCommand.CommandType.JOIN_OBSERVER, gameID, savedUsername, null);
            }

            webSocket.sendMessage(userGameCommand);

            inGameMenu(scanner);
        } catch(DeploymentException | URISyntaxException | IOException e) {
            System.out.println("Error opening client-side webSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getId(Scanner scanner) {
        System.out.println("Game ID: ");
        System.out.print(">>>  ");

        return Integer.parseInt(scanner.nextLine());
    }

    private void listGames() {
        ResponseRequest request = HttpConnection.getRequest("/game", "GET", null, authToken);

        if (request.statusCode() == 200) {
            ArrayList<GameData> games = gson.fromJson(request.responseBody(), GameListResponse.class).games();

            for (GameData gameData : games) {
                System.out.println("Game ID: " + gameData.gameID());
                System.out.println("\tGame Name: " + gameData.gameName());
                System.out.println("\tWhite Player username: " + gameData.whiteUsername());
                System.out.println("\tBlack Player username: " + gameData.blackUsername());
            }

            if (games.isEmpty()) {
                System.out.println("No games yet!");
            }
        } else {
            printErrorMessages(request.statusCode());
        }
    }

    private void createGame(Scanner scanner) {
        String gameName = null;
        int gameID = -1;

        while (gameName == null) {
            System.out.println("Name for game: ");
            System.out.print(">>>  ");
            gameName = scanner.nextLine();
            if (gameName == null || gameName.isEmpty()) {
                gameName = null;
            }
        }
        ChessGame chessGame = new ChessGame();
        chessGame.getBoard().resetBoard();

        GameData gameData = new GameData(gameID, null, null, gameName, chessGame);
        String jsonData = gson.toJson(gameData);

        ResponseRequest request = HttpConnection.getRequest("/game", "POST", jsonData, authToken);

        if (request.statusCode() == 200) {
            gameID = gson.fromJson(request.responseBody(), GameIDResponse.class).gameID();
            System.out.println("New game ID: " + gameID);
        } else {
            printErrorMessages(request.statusCode());
        }
    }

    private void logout() {
        ResponseRequest request = HttpConnection.getRequest("/session", "DELETE", "", authToken);

        if (request.statusCode() != 200) {
            printErrorMessages(request.statusCode());
        } else {
            System.out.println("Successfully logged out!");
            authToken = "";
            savedUsername = "";
        }
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

        ResponseRequest request = HttpConnection.getRequest("/user", "POST", jsonData, authToken);

        if (request.statusCode() == 200) {
            authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
            savedUsername = username;
        } else {
            printErrorMessages(request.statusCode());
        }
    }

    private void login(Scanner scanner) {
        String[] userInfo = getUserInfo(scanner);
        String username = userInfo[0];
        String password = userInfo[1];

        UserData userData = new UserData(username, password, null);
        String jsonData = gson.toJson(userData);

        ResponseRequest request = HttpConnection.getRequest("/session", "POST", jsonData, authToken);

        if (request.statusCode() == 200) {
            authToken = gson.fromJson(request.responseBody(), AuthData.class).authToken();
            savedUsername = username;
        } else {
            printLoginErrorMessages(request.statusCode());
        }
    }

    private void leave() {
        UserGameCommand command = new UserGameCommand(null, UserGameCommand.CommandType.LEAVE, 0, null, null);
        try {
            webSocket.sendMessage(command);
            webSocket = null;
            game = null;
            color = null;
        } catch(Exception e) {
            System.out.println("Leave failed, error thrown");
        }
    }

    private void movePiece(Scanner scanner) {
        System.out.println("Where is the piece you want to move?");
        ChessPosition startPosition = getPositionFromUser(scanner);
        
        ChessPiece piece = game.getBoard().getPiece(startPosition);
        boolean isPawn = (piece.getPieceType() == ChessPiece.PieceType.PAWN);

        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if (color.equalsIgnoreCase("white")) {
            if (pieceColor != ChessGame.TeamColor.WHITE) {
                System.out.println("Error: That's not your piece");
                return;
            }
        } else {
            if (pieceColor != ChessGame.TeamColor.BLACK) {
                System.out.println("Error: That's not your piece");
                return;
            }
        }

        System.out.println("Where do you want to move your piece?");
        ChessPosition endPosition = getPositionFromUser(scanner);

        ChessPiece.PieceType pieceType = null;
        int row = endPosition.row();


        if (isPawn) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 8) || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 1)) {
                ArrayList<String> possiblePromotions = new ArrayList<>();
                possiblePromotions.add("knight");
                possiblePromotions.add("bishop");
                possiblePromotions.add("rook");
                possiblePromotions.add("queen");

                while(true) {
                    System.out.println("What piece would you like to promote to: knight, bishop, rook, queen");
                    System.out.print(">>>  ");

                    String promote = scanner.nextLine();
                    promote = promote.toLowerCase();

                    if (!possiblePromotions.contains(promote)) {
                        continue;
                    }

                    switch (promote) {
                        case "knight" -> pieceType = ChessPiece.PieceType.KNIGHT;
                        case "bishop" -> pieceType = ChessPiece.PieceType.BISHOP;
                        case "rook"   -> pieceType = ChessPiece.PieceType.ROOK;
                        case "queen"  -> pieceType = ChessPiece.PieceType.QUEEN;
                    }
                    break;
                }
            }
        }

        ChessMove move = new ChessMove(startPosition, endPosition, pieceType);
        ChessGame tempGame = new ChessGame(game);
        try {
            game.makeMove(move);

            UserGameCommand command = new UserGameCommand(authToken, UserGameCommand.CommandType.MAKE_MOVE, -1, savedUsername, game);
            webSocket.sendMessage(command);
        } catch(InvalidMoveException ime) {
            System.out.println("Error: " + ime.getMessage());
        } catch(IOException | RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            game = tempGame;
        }
    }

    private ChessPosition getPositionFromUser(Scanner scanner) {
        HashMap<Character, Integer> letterToNumberMap = new HashMap<>();
        letterToNumberMap.put('a', 1);
        letterToNumberMap.put('b', 2);
        letterToNumberMap.put('c', 3);
        letterToNumberMap.put('d', 4);
        letterToNumberMap.put('e', 5);
        letterToNumberMap.put('f', 6);
        letterToNumberMap.put('g', 7);
        letterToNumberMap.put('h', 8);

        while (true) {
            System.out.println("(Do only letter then number, i.e. e7)");
            System.out.print(">>>  ");

            String location = scanner.nextLine();

            char column = location.charAt(0);
            int col = letterToNumberMap.get(column);
            int row = Integer.parseInt(location.substring(1, 2));

            if (location.length() == 2) {
                return new ChessPosition(row, col);
            }
        }
    }

    private void highlight(Scanner scanner) {
        ChessPosition position = getPositionFromUser(scanner);
        ArrayList<ChessMove> validMoves = (ArrayList<ChessMove>) game.validMoves(position);
        boolean isWhite = color.equalsIgnoreCase("white");

        ChessBoardPicture.init(game.getBoard(), isWhite, validMoves, position);
    }

    private void redraw() {
        boolean isWhite = color.equalsIgnoreCase("white");

        ChessBoardPicture.init(game.getBoard(), isWhite, new ArrayList<>(), null);
    }

    private void resign() {
        try {
            game.setGameOver(true);
            UserGameCommand command = new UserGameCommand(authToken, UserGameCommand.CommandType.RESIGN, -1, savedUsername, game);
            webSocket.sendMessage(command);
        } catch(IOException e) {
            System.out.println("Error: " + e.getMessage());
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

    private void help(int helpPage) {
        if (helpPage == 0) {
            System.out.println("\t register - make a brand new account with a new username, and whatever password/email you want! No requirements!");
            System.out.println("\t login    - using your already existing account information, login to the chess server");
            System.out.println("\t quit     - closes the application on your device");
        } else if (helpPage == 1) {
            System.out.println("\t logout   - logout from the chess server so you can access the pre-login menu again");
            System.out.println("\t create   - creates a brand new game with a gameName you give it");
            System.out.println("\t list     - lists all games, played or unplayed, in the database");
            System.out.println("\t join     - join a game you or someone else created by the ID of the game");
            System.out.println("\t watch    - watch a game as it happens");
        } else if (helpPage == 2) {
            System.out.println("\t redraw    - redraws the current chess board to make sure it is the current version");
            System.out.println("\t make move - pick a square, then pick the square that piece is moving to");
            System.out.println("\t highlight - pick a square, highlight all legal moves for a piece in that position");
            System.out.println("\t resign    - concede defeat to your opponent. Board stays drawn and notifications are sent");
            System.out.println("\t leave     - leave the game, does not have you resign so another player can take your place");
        }
    }

    private String httpCodeMessages(int httpCode) {
        switch (httpCode) {
            case (400) -> {
                return "Error: bad request, something went wrong";
            }
            case (401) -> {
                return "Error: unauthorized. Please login (you may need to choose the logout menu item first, it will throw an error but you can login again from there)";
            }
            case (403) -> {
                return "Error: already taken";
            }
            default -> {
                return "Error: description. Something went horribly wrong. Please contact the sysadmin at (999)999-9999";
            }
        }
    }

    private void printErrorMessages(int httpCode) {
        System.out.println("Error code: " + httpCode);
        System.out.println(httpCodeMessages(httpCode));
    }

    private void printLoginErrorMessages(int httpCode) {
        System.out.println("Error code: " + httpCode);
        if (httpCode == 401) {
            System.out.println("Username does not match password in our database. Please try again or register!");
        } else {
            System.out.println("Something went wrong. Please contact sysadmin");
        }
    }

    public static void saveGame(ChessGame game) {
        ClientMenu.game = game;
    }
}