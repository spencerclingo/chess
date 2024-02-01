package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessMove previousMove;
    private ArrayList<ChessMove> allMoves;
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

        for (int moveNum = 0; moveNum < allValidMoves.size(); moveNum++) {
            if (doMove(allValidMoves.get(moveNum))) {
                allValidMoves.remove(moveNum);
                moveNum--;
            }
        }

        System.out.println("This is all validMoves that validMoves has: " + allValidMoves);

        return allValidMoves;
    }

    private boolean doMove(ChessMove move) {
        ChessPiece.PieceType pieceType = currentBoard.getPiece(move.getStartPosition()).getPieceType();
        ChessPiece takenPiece = currentBoard.getPiece(move.getEndPosition());

        currentBoard.makeMove(move);
        if (!isInCheck(currentBoard.getPiece(move.getEndPosition()).getTeamColor())) {
            undoMove(move, pieceType, takenPiece);
            return false; // They are not in check
        }
        undoMove(move, pieceType, takenPiece);
        return true; // They are in check
    }

    private void undoMove(ChessMove move, ChessPiece.PieceType pieceType, ChessPiece takenPiece) {
        ChessMove moveUndo = move.getReverseMove();
        currentBoard.makeMove(moveUndo);
        currentBoard.getPiece(moveUndo.getEndPosition()).changePieceType(pieceType);
        currentBoard.addPiece(moveUndo.getStartPosition(), takenPiece);
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
        // This is where you change the team turn
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        System.out.println("Checking if this team is in check: " + teamColor);

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
        System.out.println("Is in Check: " + isInCheck(teamColor));
        System.out.println("Is in Stalemate: " + isInStalemate(teamColor));
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
            //System.out.println("This is the valid moves according to isInStalemate: " + allValidMoves);
            if (!allValidMoves.isEmpty()) {
                return false;
            }
        }

        return true;
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

    public ArrayList<ChessMove> getPossibleMoves() {
        return allMoves;
    }

    public void setPossibleMoves(ArrayList<ChessMove> possibleMoves) {
        this.allMoves=possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame=(ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getPreviousMove(), chessGame.getPreviousMove()) && Objects.equals(allMoves, chessGame.allMoves) && Objects.equals(currentBoard, chessGame.currentBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getPreviousMove(), allMoves, currentBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", previousMove=" + previousMove +
                ", allMoves=" + allMoves +
                ", currentBoard=" + currentBoard +
                '}';
    }
}
