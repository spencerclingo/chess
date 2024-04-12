package clientConnection;

import chess.*;
import ui.ChessBoardPicture;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class InGameMenu {
    static void leave() {
        UserGameCommand command = new UserGameCommand(null, UserGameCommand.CommandType.LEAVE, 0, ClientMenu.savedUsername, null);
        try {
            ClientMenu.webSocket.sendMessage(command);
            ClientMenu.webSocket = null;
            ClientMenu.game = null;
            ClientMenu.color = "";
        } catch(Exception e) {
            System.out.println("Leave failed, error thrown");
        }
    }

    static void movePiece(Scanner scanner) {
        ChessPiece piece;
        ChessPosition startPosition;
        do {
            System.out.println("Where is the piece you want to move?");
            startPosition = getPositionFromUser(scanner);

            piece = ClientMenu.game.getBoard().getPiece(startPosition);
        } while (piece == null);
        boolean isPawn = (piece.getPieceType() == ChessPiece.PieceType.PAWN);

        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if (ClientMenu.color.equalsIgnoreCase("white")) {
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
        ChessGame tempGame = new ChessGame(ClientMenu.game);
        try {
            ClientMenu.game.makeMove(move);

            UserGameCommand command = new UserGameCommand(ClientMenu.authToken, UserGameCommand.CommandType.MAKE_MOVE, -1, ClientMenu.savedUsername, ClientMenu.game);
            ClientMenu.webSocket.sendMessage(command);
        } catch(InvalidMoveException ime) {
            System.out.println("Error: " + ime.getMessage());
        } catch(IOException | RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            ClientMenu.game = tempGame;
        }
    }

    private static ChessPosition getPositionFromUser(Scanner scanner) {
        HashMap<Character, Integer> letterToNumberMap = new HashMap<>();
        letterToNumberMap.put('a', 8);
        letterToNumberMap.put('b', 7);
        letterToNumberMap.put('c', 6);
        letterToNumberMap.put('d', 5);
        letterToNumberMap.put('e', 4);
        letterToNumberMap.put('f', 3);
        letterToNumberMap.put('g', 2);
        letterToNumberMap.put('h', 1);

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

    static void highlight(Scanner scanner) {
        ChessPosition position = getPositionFromUser(scanner);
        ArrayList<ChessMove> validMoves = (ArrayList<ChessMove>) ClientMenu.game.validMoves(position);
        boolean isWhite = ClientMenu.color.equalsIgnoreCase("white");

        ChessBoardPicture.init(ClientMenu.game.getBoard(), isWhite, validMoves, position);
    }

    static void redraw() {
        boolean isWhite;
        if (ClientMenu.color == null) {
            isWhite = true;
        } else {
            isWhite = ClientMenu.color.equalsIgnoreCase("white");
        }

        ChessBoardPicture.init(ClientMenu.game.getBoard(), isWhite, new ArrayList<>(), null);
    }

    static void resign(Scanner scanner) {
        System.out.println("Are you sure you want to resign? (y/n)");
        System.out.print(">>>  ");

        String response = scanner.nextLine();

        if (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("yes")) {
            System.out.println("Okay, not resigning");
            return;
        }

        try {
            UserGameCommand command = new UserGameCommand(ClientMenu.authToken, UserGameCommand.CommandType.RESIGN, -1, ClientMenu.savedUsername, ClientMenu.game);
            ClientMenu.webSocket.sendMessage(command);
        } catch(IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
