package response;

import chess.ChessGame;

public record WebSocketJoinGameResponse(ChessGame game, String notification) {
}
