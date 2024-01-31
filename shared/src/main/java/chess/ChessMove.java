package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;
    private ChessPiece.PieceType startPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece, ChessPiece.PieceType startPiece) {
        this.startPosition=startPosition;
        this.endPosition=endPosition;
        this.promotionPiece=promotionPiece;
        this.startPiece=startPiece;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition=startPosition;
        this.endPosition=endPosition;
        this.promotionPiece=promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public ChessPiece.PieceType getStartPiece() {
        return startPiece;
    }

    public ChessMove getReverseMove() {
        if (promotionPiece == null) {
            return new ChessMove(endPosition, startPosition, startPiece);
        } else {
            return new ChessMove(endPosition, startPosition, startPiece, promotionPiece);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove=(ChessMove) o;
        return Objects.equals(getStartPosition(), chessMove.getStartPosition()) && Objects.equals(getEndPosition(), chessMove.getEndPosition()) && getPromotionPiece() == chessMove.getPromotionPiece();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartPosition(), getEndPosition(), getPromotionPiece());
    }

    @Override
    public String toString() {
        if (promotionPiece != null) {
            return "ChessMove{" +
                    "startPosition=" + startPosition.toString() +
                    ", endPosition=" + endPosition.toString() +
                    ", promotionPiece=" + promotionPiece +
                    '}';
        }
        return "ChessMove{" +
                "startPosition=" + startPosition.toString() +
                ", endPosition=" + endPosition.toString() +
                '}';
    }

}