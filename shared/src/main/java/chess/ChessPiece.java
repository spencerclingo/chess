package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;
    private ChessPosition position;

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

    public ChessPosition getPosition() {
        return position;
    }

    public void setPosition(ChessPosition position) {
        this.position=position;
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
        if(myPosition.hasPiece()) {
            switch (myPosition.getPieceInPosition().pieceType) {
                case PAWN:
                    throw new RuntimeException("Pawn Not implemented");
                    //break;
                case KNIGHT:
                    throw new RuntimeException("Knight Not implemented");
                    //break;
                case BISHOP:
                    pieceMovesArray = diagonalMove(myPosition.getPieceInPosition(), myPosition, board);
                    break;
                case ROOK:
                    throw new RuntimeException("Rook Not implemented");
                    //break;
                case QUEEN:
                    throw new RuntimeException("Queen Not implemented");
                    //break;
                case KING:
                    throw new RuntimeException("King Not implemented");
                    //break;
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
    public ArrayList<ChessMove> diagonalMove(ChessPiece piece, ChessPosition position, ChessBoard board) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();

        ChessPosition oldPosition = new ChessPosition(position.getRow(),position.getCol());
        //Moving up and to the right
        while(oldPosition.getRow() < 8 && oldPosition.getCol() < 8) {
            ChessPosition checkPosition=new ChessPosition(oldPosition.getRow() + 1, oldPosition.getCol() + 1);
            ChessMove newMove = Movement(oldPosition, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = checkPosition;
            } else {
                break;
            }
        }
        //Moving up and to the left
        oldPosition = position;
        while(oldPosition.getRow() < 8 && oldPosition.getCol() > 1) {
            ChessPosition checkPosition=new ChessPosition(oldPosition.getRow() + 1, oldPosition.getCol() - 1);
            ChessMove newMove = Movement(oldPosition, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = checkPosition;
            } else {
                break;
            }
        }
        //Moving down and to the left
        oldPosition = position;
        while(oldPosition.getRow() > 1 && oldPosition.getCol() > 1) {
            ChessPosition checkPosition=new ChessPosition(oldPosition.getRow() - 1, oldPosition.getCol() - 1);
            ChessMove newMove = Movement(oldPosition, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = checkPosition;
            } else {
                break;
            }
        }
        //Moving down and to the right
        oldPosition = position;
        while(oldPosition.getRow() > 1 && oldPosition.getCol() < 8) {
            ChessPosition checkPosition=new ChessPosition(oldPosition.getRow() - 1, oldPosition.getCol() + 1);
            ChessMove newMove = Movement(oldPosition, checkPosition, board, piece);
            if (newMove != null) {
                possibleMoves.add(newMove);
            } else {
                break;
            }
            if (MoveOccupied(checkPosition, board)) {
                oldPosition = checkPosition;
            } else {
                break;
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
}
