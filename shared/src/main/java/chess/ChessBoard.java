package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPosition[][] chessBoardArray = new ChessPosition[9][9];
    private ChessPiece[][] chessPiecesArray = new ChessPiece[10][10];

    public ChessBoard() {
        for(int rows = 1; rows < 9; rows++) {
            for(int cols = 1; cols < 9; cols++) {
                ChessPosition position = new ChessPosition(rows, cols);
                chessBoardArray[rows][cols] = position;
                chessPiecesArray[rows][cols] = null;
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
        chessPiecesArray[position.getRow()][position.getCol()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessPiecesArray[position.getRow()][position.getCol()];
    }
    public void movePiece(ChessPosition oldPosition, ChessPosition newPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }

    public ChessPosition getPosition(ChessPosition position) {
        return chessBoardArray[position.getRow()][position.getCol()];
    }
    public ChessPosition getPosition(int row, int col) {
        return chessBoardArray[row][col];
    }

    public ChessPiece pieceInPosition(int row, int col) {
        return chessPiecesArray[row][col];
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;
        return Arrays.equals(chessBoardArray, that.chessBoardArray) && Arrays.equals(chessPiecesArray, that.chessPiecesArray);
    }

    @Override
    public int hashCode() {
        int result=Arrays.hashCode(chessBoardArray);
        result=31 * result + Arrays.hashCode(chessPiecesArray);
        return result;
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "chessBoardArray=" + Arrays.deepToString(chessBoardArray) +
                ", chessPiecesArray=" + Arrays.deepToString(chessPiecesArray) +
                '}';
    }
}
