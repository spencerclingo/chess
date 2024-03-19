import chess.*;
import clientConnection.ChessClient;
import server.Server;

import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        try {
            int port = 8080;
            Server server = new Server();
            server.run(port);
            new ChessClient(port);
        } catch(URISyntaxException e) {
            System.out.print("Error creating ChessClient: ");
            System.out.println(e.getMessage());
        }
    }
}