package chess;

//import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessMove previousMove;
    private ChessBoard currentBoard;


    public ChessGame() {
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
        previousMove = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> allValidMoves;
        ChessPiece piece = currentBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        allValidMoves = (ArrayList<ChessMove>) piece.pieceMoves(currentBoard, startPosition);

        ChessMove enPassant = enPassant(startPosition);

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            ArrayList<ChessMove> castlingMoves = castling(startPosition);
            if (castlingMoves != null) {
                allValidMoves.addAll(castlingMoves);
            }
        }

        if (enPassant != null) {
            allValidMoves.add(enPassant);
        }

        for (int moveNum = 0; moveNum < allValidMoves.size(); moveNum++) {
            if (doMove(allValidMoves.get(moveNum))) {
                allValidMoves.remove(moveNum);
                moveNum--;
            }
        }

        return allValidMoves;
    }

    private boolean doMove(ChessMove move){
        if (move.isEnPassant()) {
            return testEnPassantMove(move);
        }

        ChessPiece.PieceType pieceType = currentBoard.getPiece(move.getStartPosition()).getPieceType();
        ChessPiece takenPiece = currentBoard.getPiece(move.getEndPosition());

        currentBoard.makeMove(move, false);
        if (!isInCheck(currentBoard.getPiece(move.getEndPosition()).getTeamColor())) {
            undoMove(move, pieceType, takenPiece);
            return false; // They are not in check
        }
        undoMove(move, pieceType, takenPiece);
        return true; // They are in check
    }

    private void undoMove(ChessMove move, ChessPiece.PieceType pieceType, ChessPiece takenPiece) {
        ChessMove moveUndo = move.getReverseMove();
        currentBoard.makeMove(moveUndo, false);
        currentBoard.getPiece(moveUndo.getEndPosition()).changePieceType(pieceType);
        currentBoard.addPiece(moveUndo.getStartPosition(), takenPiece);
    }

    private boolean testEnPassantMove(ChessMove move) {
        ChessBoard testBoard = currentBoard.copy();

        testBoard.makeMove(move, false);
        return isInCheck(testBoard.getPiece(move.getEndPosition()).getTeamColor());
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (teamTurn != currentBoard.getPiece(move.getStartPosition()).getTeamColor()) {
            throw new InvalidMoveException("Not your turn!");
        }
        if (currentBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException();
        }

        ArrayList<ChessMove> allValidMoves =(ArrayList<ChessMove>) validMoves(move.getStartPosition());
        boolean validMove = false;


        for (ChessMove testMove : allValidMoves) {
            if (testMove.equals(move)) {
                validMove = true;
                move = testMove;
                break;
            }
        }

        if (!validMove) {
            throw new InvalidMoveException("That move is illegal!");
        }

        currentBoard.makeMove(move, true);
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }

        if (move.isEnPassant()) {
            currentBoard.removePiece(previousMove.getEndPosition());
        }

        setPreviousMove(move);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor oppositeColor;

        if (teamColor == TeamColor.WHITE) {
            oppositeColor = TeamColor.BLACK;
        } else {
            oppositeColor = TeamColor.WHITE;
        }

        ArrayList<ChessMove> allMoves = currentBoard.allTeamMoves(oppositeColor);
        ChessPosition kingPosition = currentBoard.findKing(teamColor);

        if (kingPosition == null) {
            return false;
        }

        for (ChessMove move : allMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (isInCheck(teamColor) && isInStalemate(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ArrayList<ChessPosition> allPositions = currentBoard.allTeamPiecePositions(teamColor);
        ArrayList<ChessMove> allValidMoves = new ArrayList<>();

        if (teamColor != teamTurn) {
            return false;
        }

        for (ChessPosition position : allPositions) {
            allValidMoves.addAll(validMoves(position));
            if (!allValidMoves.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private ChessMove enPassant(ChessPosition startPosition) {

        ChessPiece piece = currentBoard.getPiece(startPosition);
        ChessMove newMove;

        if (piece == null || previousMove == null) {
            return null;
        }


        ChessPiece possiblePawn = currentBoard.getPiece(previousMove.getEndPosition());


        if (possiblePawn == null) {
            return null;
        }

        if (possiblePawn.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (abs(previousMove.getEndPosition().row() - previousMove.getStartPosition().row()) == 2) {
                if (startPosition.col() + 1 == previousMove.getEndPosition().col()) {
                    ChessPosition newPosition = new ChessPosition((abs(previousMove.getEndPosition().row() +
                            previousMove.getStartPosition().row()) / 2), previousMove.getEndPosition().col());
                    newMove = new ChessMove(startPosition, newPosition, null);
                    newMove.setEnPassant(true);
                } else if (startPosition.col() - 1 == previousMove.getEndPosition().col()) {
                    ChessPosition newPosition = new ChessPosition((abs(previousMove.getEndPosition().row() +
                            previousMove.getStartPosition().row()) / 2), previousMove.getEndPosition().col());
                    newMove = new ChessMove(startPosition, newPosition, null);
                    newMove.setEnPassant(true);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
        return null;
        }

        return newMove;
    }


    private ArrayList<ChessMove> castling(ChessPosition startPosition) {
        ArrayList<ChessMove> allValidMoves = new ArrayList<>();
        ChessPiece piece = currentBoard.getPiece(startPosition);
        int startRow;
        int rightRookStartCol = 8;
        int rightKingEndCol = 7;
        int rightRookEndCol = 6;
        int leftRookStartCol = 1;
        int leftKingEndCol = 3;
        int leftRookEndCol = 4;

        if (piece.getTeamColor() == TeamColor.WHITE) {
            startRow = 1;
        } else {
            startRow = 8;
        }

        if (isInCheck(piece.getTeamColor())) {
            return null;
        }

        if (piece.hasPieceNotMoved()) {
            castlingHelper(startPosition, allValidMoves, startRow, rightRookStartCol, rightKingEndCol, rightRookEndCol);
            castlingHelper(startPosition, allValidMoves, startRow, leftRookStartCol, leftKingEndCol, leftRookEndCol);
        }

        return allValidMoves;
    }

    private void castlingHelper(ChessPosition startPosition, ArrayList<ChessMove> allValidMoves, int startRow, int rightRookStartCol, int rightKingEndCol, int rightRookEndCol) {
        // Check that piece exists
        if (currentBoard.getPiece(startRow, rightRookStartCol) != null) {
            ChessPiece potentialRook = currentBoard.getPiece(startRow, rightRookStartCol);

            // Check for the rook to not have moved
            if (potentialRook.getPieceType() == ChessPiece.PieceType.ROOK && potentialRook.hasPieceNotMoved()) {

                // Check for pieces in-between
                if (currentBoard.getPiece(startRow, rightRookEndCol) == null &&
                        currentBoard.getPiece(startRow, rightKingEndCol) == null) {

                    // Check for checks in-between
                    ChessMove rookEnd = new ChessMove(startPosition, new ChessPosition(startRow, rightRookEndCol), null);
                    ChessMove kingEnd = new ChessMove(startPosition, new ChessPosition(startRow, rightKingEndCol), null);
                    if (!doMove(rookEnd) && !doMove(kingEnd)) {

                        kingEnd.setCastling();
                        allValidMoves.add(kingEnd);
                    }
                }
            }
        }
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    public ChessMove getPreviousMove() {
        return previousMove;
    }

    public void setPreviousMove(ChessMove previousMove) {
        this.previousMove=previousMove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame=(ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getPreviousMove(), chessGame.getPreviousMove()) && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getPreviousMove(), currentBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", previousMove=" + previousMove +
                ", currentBoard=" + currentBoard +
                '}';
    }
}
