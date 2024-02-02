package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece{

    private final ChessGame.TeamColor color;
    private PieceType type;
    private boolean hasMoved = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessPiece copy() {
        ChessPiece clonedPiece = new ChessPiece(color, type);
        clonedPiece.hasMoved = this.hasMoved;
        return clonedPiece;
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    public void changePieceType(PieceType promotionPiece) {
        type = promotionPiece;
    }

    public boolean hasPieceNotMoved() {
        return !hasMoved;
    }

    public void pieceMoved() {
        hasMoved = true;
    }



    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        switch (piece.type) {
            case BISHOP:
                possibleMoves = diagonalMoves(board, myPosition, piece);
                break;
            case ROOK:
                possibleMoves = straightMoves(board, myPosition, piece);
                break;
            case QUEEN:
                possibleMoves = diagonalMoves(board, myPosition, piece);
                possibleMoves.addAll(straightMoves(board, myPosition, piece));
                break;
            case KING, KNIGHT:
                possibleMoves = singleMove(board, myPosition, piece);
                break;
            case PAWN:
                possibleMoves = pawnMoves(board, myPosition, piece);
                break;
        }

        return possibleMoves;
    }

    /**
     * Method for determining the diagonal moves a piece can make
     * Used for: Bishop, Queen
     *
     * @param board ChessBoard
     * @param startPosition Position the piece is on
     * @param piece The piece in question
     * @return ArrayList of ChessMove
     */
    private ArrayList<ChessMove> diagonalMoves(ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, 1, 1));  // Up and to the right
        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, 1, -1)); // Up and left
        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, -1, -1)); // Down and left
        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, -1, 1));  // Down and right

        return possibleMoves;
    }

    /**
     * Makes the repeated, straight line moves for pieces
     * (Rook, Queen)
     *
     * @param board         ChessBoard
     * @param startPosition Position the piece is on
     * @param piece         The piece in question
     * @return ArrayList of ChessMove
     */
    private ArrayList<ChessMove> straightMoves(ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, 1, 0));  // Up and to the right
        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, 0, -1)); // Up and left
        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, 0, 1)); // Down and left
        possibleMoves.addAll(generateMovesInDirection(board, startPosition, piece, -1, 0));  // Down and right

        return possibleMoves;
    }

    private ArrayList<ChessMove> generateMovesInDirection(
            ChessBoard board, ChessPosition startPosition, ChessPiece piece, int rowDirection, int colDirection) {

        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        ChessPosition position = startPosition.copy();

        while (position.getRow() + rowDirection >= 1 && position.getRow() + rowDirection <= 8 &&
                position.getCol() + colDirection >= 1 && position.getCol() + colDirection <= 8) {

            ChessPosition endPosition = position.changedCopy(rowDirection, colDirection);

            if (board.getPiece(endPosition) == null) {
                possibleMoves.add(new ChessMove(startPosition, endPosition, null));
            } else if (board.getPiece(endPosition).getTeamColor() != piece.getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, endPosition, null));
                break;
            } else {
                break;
            }

            position = endPosition.copy();
        }

        return possibleMoves;
    }

    /**
     * Makes ArrayList of possible moves for either Knight or King
     *
     * @param board ChessBoard
     * @param startPosition Position the piece is on
     * @param piece The piece in question
     * @return ArrayList of ChessMove
     */
    private ArrayList<ChessMove> singleMove(ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] movementOptions = getMovementOptions(piece);

        for (int[] options : movementOptions) {
            if (startPosition.getRow() + options[0] >= 9 || startPosition.getRow() + options[0] <= 0 ||
                    startPosition.getCol() + options[1] >= 9 || startPosition.getCol() + options[1] <= 0) {
                continue;
            }

            ChessPosition newPosition = startPosition.changedCopy(options[0], options[1]);
            if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            }
        }

        return possibleMoves;
    }

    private static int[][] getMovementOptions(ChessPiece piece) {
        int[][] movementOptions;

        if (piece.getPieceType() == PieceType.KING) {
            movementOptions = new int[][] {
                    {1,1}, {1,0}, {1, -1}, // Moving up
                    {0,1}, {0,-1},         // Moving to the side
                    {-1,1},{-1,0},{-1,-1}  // Moving down
            };
        } else {
            movementOptions = new int[][] {
                    {2, 1}, {2, -1}, // Move 2 up
                    {1, 2}, {1, -2}, // Move 1 up
                    {-1,2}, {-1,-2}, // Move 1 down
                    {-2,1}, {-2,-1}, // Move 2 down
            };
        }
        return movementOptions;
    }

    /**
     * Gets the possible pawn moves
     *
     * @param board ChessBoard
     * @param startPosition Position the piece is on
     * @param piece The piece in question
     * @return ArrayList of ChessMove
     */
    private ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int forward;
        int startRow;
        int promotionRow;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            forward = 1;
            startRow = 2;
            promotionRow = 7;
        } else {
            forward = -1;
            startRow = 7;
            promotionRow = 2;
        }

        // Forward moves
        ChessPosition moveOne = startPosition.changedCopy(forward,0);
        if (board.getPiece(moveOne) == null) {
            if (startPosition.getRow() == promotionRow) { // If the pawn is moving to where it can promote
                possibleMoves.add(new ChessMove(startPosition, moveOne, PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(startPosition, moveOne, PieceType.ROOK));
                possibleMoves.add(new ChessMove(startPosition, moveOne, PieceType.BISHOP));
                possibleMoves.add(new ChessMove(startPosition, moveOne, PieceType.QUEEN));
            } else { // cannot promote from this position
                ChessPosition moveTwo = startPosition.changedCopy(2*forward, 0);
                possibleMoves.add(new ChessMove(startPosition, moveOne, null));
                if (board.getPiece(moveTwo) == null && startPosition.getRow() == startRow) {
                    possibleMoves.add(new ChessMove(startPosition, moveTwo, null));
                }
            }
        }

        // Attacking right
        ChessPosition attackRight = startPosition.changedCopy(forward, 1);
        if (board.getPiece(attackRight) != null && board.getPiece(attackRight).getTeamColor() != piece.getTeamColor()) {
            if (startPosition.getRow() == promotionRow) { // If the pawn is moving to where it can promote
                possibleMoves.add(new ChessMove(startPosition, attackRight, PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(startPosition, attackRight, PieceType.ROOK));
                possibleMoves.add(new ChessMove(startPosition, attackRight, PieceType.BISHOP));
                possibleMoves.add(new ChessMove(startPosition, attackRight, PieceType.QUEEN));
            } else { // cannot promote from this position
                possibleMoves.add(new ChessMove(startPosition, attackRight, null));
            }
        }

        // Attacking left
        ChessPosition attackLeft = startPosition.changedCopy(forward, -1);
        if (board.getPiece(attackLeft) != null && board.getPiece(attackLeft).getTeamColor() != piece.getTeamColor()) {
            if (startPosition.getRow() == promotionRow) { // If the pawn is moving to where it can promote
                possibleMoves.add(new ChessMove(startPosition, attackLeft, PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(startPosition, attackLeft, PieceType.ROOK));
                possibleMoves.add(new ChessMove(startPosition, attackLeft, PieceType.BISHOP));
                possibleMoves.add(new ChessMove(startPosition, attackLeft, PieceType.QUEEN));
            } else { // cannot promote from this position
                possibleMoves.add(new ChessMove(startPosition, attackLeft, null));
            }
        }

        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
