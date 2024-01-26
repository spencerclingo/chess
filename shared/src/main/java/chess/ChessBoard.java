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
        for (int col = 1; col <= 8; col++) {
            chessPiecesArray[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, getPieceTypeForColumn(col));
            chessPiecesArray[8][col] = new ChessPiece(ChessGame.TeamColor.BLACK, getPieceTypeForColumn(col));
            chessPiecesArray[2][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            chessPiecesArray[7][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }
    private ChessPiece.PieceType getPieceTypeForColumn(int col) {
        switch (col) {
            case 1:
            case 8:
                return ChessPiece.PieceType.ROOK;
            case 2:
            case 7:
                return ChessPiece.PieceType.KNIGHT;
            case 3:
            case 6:
                return ChessPiece.PieceType.BISHOP;
            case 4:
                return ChessPiece.PieceType.QUEEN;
            case 5:
                return ChessPiece.PieceType.KING;
            default:
                throw new IllegalArgumentException("Invalid column number: " + col);
        }
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
        return Arrays.deepEquals(chessBoardArray, that.chessBoardArray) && Arrays.deepEquals(chessPiecesArray, that.chessPiecesArray);
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
