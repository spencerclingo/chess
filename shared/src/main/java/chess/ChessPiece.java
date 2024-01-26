package chess;

import jdk.jshell.Diag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
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

    /**
     * The two different chess team options
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    public void changePieceType(ChessPiece.PieceType pieceType) {
        this.pieceType = pieceType;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        myPosition = board.getPosition(myPosition);
        ArrayList<ChessMove> pieceMovesArray = new ArrayList<>();
        if(board.getPiece(myPosition) != null) {
            switch (board.getPiece(myPosition).pieceType) {
                case PAWN:
                    pieceMovesArray = PawnMovement(board.getPiece(myPosition), myPosition, board);
                    break;
                case KNIGHT, KING:
                    pieceMovesArray = SingleMove(board.getPiece(myPosition), myPosition, board);
                    break;
                case BISHOP:
                    pieceMovesArray = DiagonalMove(board.getPiece(myPosition), myPosition, board);
                    break;
                case ROOK:
                    pieceMovesArray = StraightMove(board.getPiece(myPosition), myPosition, board);
                    break;
                case QUEEN:
                    pieceMovesArray = DiagonalMove(board.getPiece(myPosition), myPosition, board);
                    pieceMovesArray.addAll(StraightMove(board.getPiece(myPosition), myPosition, board));
                    break;
            }
        }


        return pieceMovesArray;
    }

    /**
     * Method for determining the diagonal moves a piece can make
     * Used for: Bishop, Queen
     *
     * @param piece
     * @param position
     * @param board
     * @return ArrayList of ChessMove
     */
    public ArrayList<ChessMove> DiagonalMove(ChessPiece piece, ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        ChessPosition oldPosition = new ChessPosition(position);

        //Moving up
        while(oldPosition.getRow() > 1 && oldPosition.getCol() > 1) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow() - 1, oldPosition.getCol() - 1);

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        //Moving down and to the right

        oldPosition = new ChessPosition(position);
        while(oldPosition.getRow() > 1 && oldPosition.getCol() < 8) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow() - 1, oldPosition.getCol() + 1);

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        //Moving up and to the right
        oldPosition = new ChessPosition(position);
        while(oldPosition.getRow() < 8 && oldPosition.getCol() < 8) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow() + 1, oldPosition.getCol() + 1);

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        //Moving up and to the left
        oldPosition = new ChessPosition(position);
        while(oldPosition.getRow() < 8 && oldPosition.getCol() > 1) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow() + 1, oldPosition.getCol() - 1);

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);

            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        return possibleMoves;
    }

    /**
     * Makes the repeated, straight line moves for pieces
     * (Rook, Queen)
     *
     * @param piece
     * @param position
     * @param board
     * @return ArrayList of ChessMove
     */
    public ArrayList<ChessMove> StraightMove(ChessPiece piece, ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        ChessPosition oldPosition = new ChessPosition(position);

        //Moving up
        while(oldPosition.getRow() < 8) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow() + 1, oldPosition.getCol());

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        //Moving right
        oldPosition = new ChessPosition(position);
        while(oldPosition.getCol() < 8) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow(), oldPosition.getCol() + 1);

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        //Moving down
        oldPosition = new ChessPosition(position);
        while(oldPosition.getRow() > 1) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow() - 1, oldPosition.getCol());

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        //Moving left
        oldPosition = new ChessPosition(position);
        while(oldPosition.getCol() > 1) {
            ChessPosition checkPosition = board.getPosition(oldPosition.getRow(), oldPosition.getCol() - 1);

            ChessMove newMove = Movement(position, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = new ChessPosition(checkPosition);
            } else {
                break;
            }
        }

        return possibleMoves;
    }

    /**
     * Makes ArrayList of possible moves for either Knight or King
     *
     * @param piece
     * @param position
     * @param board
     * @return ArrayList of ChessMove
     */
    public ArrayList<ChessMove> SingleMove(ChessPiece piece, ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int[][] relativePositions;

        if (piece.pieceType == PieceType.KING) {
            relativePositions = new int[][]{
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1},   // Adjacent positions (up, down, left, right)
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Diagonal positions
            };
        } else {
            relativePositions = new int[][]{
                    {1,-2}, {1,2},   // Move side then up
                    {2,-1}, {2,1},   // Move up then side
                    {-1,-2}, {-1,2}, // Move side then down
                    {-2,-1}, {-2,1}  // Move down then side
            };
        }

        for (int[] relativePos : relativePositions) {
            int newRow=position.getRow() + relativePos[0];
            int newCol=position.getCol() + relativePos[1];

            if (newRow <= 8 && newCol <= 8 && newRow > 0 && newCol > 0) {
                ChessPiece testPiece = board.pieceInPosition(newRow, newCol);
                if (testPiece == null || testPiece.getTeamColor() != piece.getTeamColor()) {
                    ChessMove newMove = new ChessMove(position, new ChessPosition(position.getRow() +
                            relativePos[0], position.getCol() + relativePos[1]), null);

                    possibleMoves.add(newMove);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Gets the possible pawn moves
     *
     * @param piece
     * @param position
     * @param board
     * @return ArrayList of ChessMove
     */
    public ArrayList<ChessMove> PawnMovement(ChessPiece piece, ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        int forward;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            forward = 1;
        } else {
            forward = -1;
        }
        int newRow = position.getRow() + forward;
        int leftAttackCol = position.getCol() - 1;
        int straight = position.getCol();
        int rightAttackCol =position.getCol() + 1;
        PieceType[] pieceTypesArray = {PieceType.QUEEN,PieceType.ROOK,PieceType.KNIGHT,PieceType.BISHOP};

        if (board.pieceInPosition(newRow, straight) == null) {
            if (newRow == 8 || newRow == 1) { // If it can promote

                for (PieceType pieceType : pieceTypesArray) {
                    ChessMove newMove=new ChessMove(position,
                            new ChessPosition(newRow, straight), pieceType);
                    possibleMoves.add(newMove);
                }
            } else { // If it cannot promote
                ChessMove newMove=new ChessMove(position,
                        new ChessPosition(newRow, straight), null);
                possibleMoves.add(newMove);

                //This is the double move thing
                if (board.pieceInPosition(position.getRow() + (2 * forward), straight) == null &&
                        (((position.getRow() == 2) && (piece.getTeamColor() == ChessGame.TeamColor.WHITE)) ||
                                ((position.getRow() == 7) && (piece.getTeamColor() == ChessGame.TeamColor.BLACK)))){
                    newMove=new ChessMove(position,
                            new ChessPosition(position.getRow() + (2 * forward), straight), null);
                    possibleMoves.add(newMove);
                }
            }
        }
        if (board.pieceInPosition(newRow, leftAttackCol) != null) {
            if (board.pieceInPosition(newRow, leftAttackCol).getTeamColor() !=
                    piece.teamColor) {
                if (newRow == 8 || newRow == 1) { // If it can promote on taking
                    for (PieceType pieceType : pieceTypesArray) {
                        ChessMove newMove=new ChessMove(position,
                                new ChessPosition(newRow, leftAttackCol), pieceType);
                        possibleMoves.add(newMove);
                    }

                } else { // If it cannot promote
                    ChessMove newMove=new ChessMove(position,
                            new ChessPosition(newRow, leftAttackCol), null);
                    possibleMoves.add(newMove);
                }

            }
        }
        if (board.pieceInPosition(newRow, rightAttackCol) != null) {
            if (board.pieceInPosition(newRow,
                    rightAttackCol).getTeamColor() != piece.teamColor) {
                if (newRow == 8 || newRow == 1) { // If it can promote on taking
                    for (PieceType pieceType : pieceTypesArray) {
                        ChessMove newMove=new ChessMove(position,
                                new ChessPosition(newRow, rightAttackCol), pieceType);
                        possibleMoves.add(newMove);
                    }
                } else { // If it cannot promote
                    ChessMove newMove=new ChessMove(position,
                            new ChessPosition(newRow, rightAttackCol), null);
                    possibleMoves.add(newMove);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Handles checking for pieces in movement
     * MUST BE CALLED WITH MoveOccupied TO ENSURE CORRECT MOVEMENT
     *
     * @param oldPosition
     * @param checkPosition
     * @param board
     * @param piece
     * @return ChessMove
     */
    public ChessMove Movement(ChessPosition oldPosition, ChessPosition checkPosition, ChessBoard board, ChessPiece piece) {
        if (piece.getPieceType() != PieceType.PAWN) {
            if (board.getPiece(checkPosition) == null) {
                return new ChessMove(oldPosition, checkPosition, null);
            } else if (board.getPiece(checkPosition).getTeamColor() != piece.getTeamColor()) {
                return new ChessMove(oldPosition, checkPosition, null);
            } else if (board.getPiece(checkPosition).getTeamColor() == piece.getTeamColor()) {
                return null;
            }
            throw new RuntimeException("Position doesn't have null piece, an opposite team piece, or a same team piece.");
        }
        throw new RuntimeException("Pawn Movement Not implemented");
    }

    /**
     * Checks if a movement loop should stop
     * If a piece is in the new location, should end loop
     * Otherwise, doesn't
     *
     * @param checkPosition
     * @param board
     * @return boolean
     */


    public boolean MoveOccupied(ChessPosition checkPosition, ChessBoard board) {
        return board.getPiece(checkPosition) == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return getTeamColor() == that.getTeamColor() && getPieceType() == that.getPieceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamColor(), getPieceType());
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }
}
