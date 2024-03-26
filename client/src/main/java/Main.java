import chess.*;
import clientConnection.ClientMenu;

import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        int port = 8080;
        new ClientMenu(port);
    }
}