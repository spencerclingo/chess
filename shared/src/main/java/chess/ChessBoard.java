package chess;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] chessPieceArray = new ChessPiece[10][10];
    public ChessBoard() {}

    public ChessBoard(ChessPiece[][] chessPieceArray) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                if (chessPieceArray[row][col] != null) {
                    this.chessPieceArray[row][col]=chessPieceArray[row][col].copy();
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessPieceArray[position.row()][position.col()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessPieceArray[position.row()][position.col()];
    }

    public ChessPiece getPiece(int row, int col) {
        return chessPieceArray[row][col];
    }

    public void removePiece (ChessPosition position) {
        chessPieceArray[position.row()][position.col()] = null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int col = 1; col < 9; col++) {
            chessPieceArray[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, resetHelper(col));
            chessPieceArray[2][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            chessPieceArray[7][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            chessPieceArray[8][col] = new ChessPiece(ChessGame.TeamColor.BLACK, resetHelper(col));
        }
    }

    private ChessPiece.PieceType resetHelper(int col) {
        return switch (col) {
            case 1, 8 -> ChessPiece.PieceType.ROOK;
            case 2, 7 -> ChessPiece.PieceType.KNIGHT;
            case 3, 6 -> ChessPiece.PieceType.BISHOP;
            case 4 -> ChessPiece.PieceType.QUEEN;
            case 5 -> ChessPiece.PieceType.KING;
            default -> throw new RuntimeException("Invalid piece");
        };
    }

    public void visualBoardFix() {
        ChessPiece kingWhite  = chessPieceArray[1][5];
        ChessPiece queenWhite = chessPieceArray[1][4];
        ChessPiece kingBlack  = chessPieceArray[8][5];
        ChessPiece queenBlack = chessPieceArray[8][4];

        chessPieceArray[1][5] = queenWhite;
        chessPieceArray[1][4] = kingWhite;
        chessPieceArray[8][5] = queenBlack;
        chessPieceArray[8][4] = kingBlack;
    }

    public void makeMove(ChessMove move, boolean permanent) {
        chessPieceArray[move.getEndPosition().row()][move.getEndPosition().col()] =
                chessPieceArray[move.getStartPosition().row()][move.getStartPosition().col()];
        if (move.getPromotionPiece() != null) {
            chessPieceArray[move.getEndPosition().row()][move.getEndPosition().col()].changePieceType(move.getPromotionPiece());
        }
        chessPieceArray[move.getStartPosition().row()][move.getStartPosition().col()] = null;
        if (permanent) {
            chessPieceArray[move.getEndPosition().row()][move.getEndPosition().col()].pieceMoved();
        }

        if (move.isEnPassant()) {
            if (move.getEndPosition().row() == 6) {
                chessPieceArray[move.getEndPosition().row() - 1][move.getEndPosition().col()] = null;
            } else {
                chessPieceArray[move.getEndPosition().row() + 1][move.getEndPosition().col()] = null;
            }
        }
        if (move.isCastling() && permanent) {
            if (move.getStartPosition().col() > move.getEndPosition().col()) {
                chessPieceArray[move.getEndPosition().row()][move.getEndPosition().col() + 1] =
                        chessPieceArray[move.getEndPosition().row()][1];
                chessPieceArray[move.getEndPosition().row()][1] = null;
            } else {
                chessPieceArray[move.getEndPosition().row()][move.getEndPosition().col() - 1] =
                        chessPieceArray[move.getEndPosition().row()][8];
                chessPieceArray[move.getEndPosition().row()][8] = null;
            }
        }
    }

    public ArrayList<ChessMove> allTeamMoves(ChessGame.TeamColor teamColor) {
        ArrayList<ChessMove> allValidMoves = new ArrayList<>();

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                if (chessPieceArray[row][col] != null && chessPieceArray[row][col].getTeamColor() == teamColor) {
                    allValidMoves.addAll(chessPieceArray[row][col].pieceMoves(this, new ChessPosition(row, col)));
                }
            }
        }

        return allValidMoves;
    }

    public ChessPosition findKing(ChessGame.TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                if (chessPieceArray[row][col] != null) {
                    if (chessPieceArray[row][col].getPieceType() == ChessPiece.PieceType.KING &&
                            chessPieceArray[row][col].getTeamColor() == teamColor) {
                        return new ChessPosition(row, col);
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<ChessPosition> allTeamPiecePositions(ChessGame.TeamColor teamColor) {
        ArrayList<ChessPosition> allPositions = new ArrayList<>();

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                if (chessPieceArray[row][col] != null && chessPieceArray[row][col].getTeamColor() == teamColor) {
                    allPositions.add(new ChessPosition(row, col));
                }
            }
        }

        return allPositions;
    }

    public ChessBoard copy() {
        return new ChessBoard(chessPieceArray);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "chessPieceArray=" + Arrays.deepToString(chessPieceArray) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;
        return Arrays.deepEquals(chessPieceArray, that.chessPieceArray);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessPieceArray);
    }

}
