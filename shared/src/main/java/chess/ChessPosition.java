package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPosition(int row, int col) {

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    @Override
    public int row() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    @Override
    public int col() {
        return col;
    }

    /**
     * Returns a new position object with integer differences
     * in the row and column. Used to move pieces
     *
     * @param rowDif Positive moves up, negative moves down
     * @param colDif Positive moves right, negative moves left
     * @return ChessPosition
     */
    public ChessPosition changedCopy(int rowDif, int colDif) {
        return new ChessPosition(row + rowDif, col + colDif);
    }

    /**
     * Returns a copy of a position so changes to it doesn't cause problems
     *
     * @return ChessPosition
     */
    public ChessPosition copy() {
        return new ChessPosition(row, col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition position=(ChessPosition) o;
        return row() == position.row() && col() == position.col();
    }

    @Override
    public int hashCode() {
        return Objects.hash(row(), col());
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
