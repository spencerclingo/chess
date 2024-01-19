package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPosition[][] chessBoardArray = new ChessPosition[9][9];

    public ChessBoard() {
        for(int rows = 1; rows < 9; rows++) {
            for(int cols = 1; cols < 9; cols++) {
                ChessPosition position = new ChessPosition(rows, cols);
                chessBoardArray[rows][cols] = position;
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
        chessBoardArray[position.getRow()][position.getCol()] = position;
        position.setPieceInPosition(piece);
        piece.setPosition(position);

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessBoardArray[position.getRow()][position.getCol()].getPieceInPosition();
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
}
