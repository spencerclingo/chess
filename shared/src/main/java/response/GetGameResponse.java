package response;

import chess.ChessGame;

public record GetGameResponse(ChessGame chessGame, int httpCode) {
}
