package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] chessPieceArray = new ChessPiece[10][10];
    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessPieceArray[position.getRow()][position.getCol()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessPieceArray[position.getRow()][position.getCol()];
    }

    public ChessPiece getPiece(int row, int col) {
        return chessPieceArray[row][col];
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
        switch(col) {
            case 1, 8: return ChessPiece.PieceType.ROOK;
            case 2, 7: return ChessPiece.PieceType.KNIGHT;
            case 3, 6: return ChessPiece.PieceType.BISHOP;
            case 4: return ChessPiece.PieceType.QUEEN;
            case 5: return ChessPiece.PieceType.KING;
            default: throw new RuntimeException("Invalid piece");
        }
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
