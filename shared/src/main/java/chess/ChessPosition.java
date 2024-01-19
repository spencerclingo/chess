package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;
    private ChessPiece pieceInPosition = null;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row=row;
    }

    public void setCol(int col) {
        this.col=col;
    }

    public ChessPiece getPieceInPosition() {
        return pieceInPosition;
    }

    public void setPieceInPosition(ChessPiece pieceInPosition) {
        this.pieceInPosition=pieceInPosition;
    }

    public void removePieceFromPosition() {
        pieceInPosition = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition position=(ChessPosition) o;
        return getRow() == position.getRow() && getCol() == position.getCol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol());
    }

    public boolean hasPiece() {
        return pieceInPosition != null;
    }
}
