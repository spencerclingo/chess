package chess;

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
        //System.out.println("This is the start position where I look for valid moves: " + startPosition);

        ArrayList<ChessMove> allValidMoves;
        ChessPiece piece = currentBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        allValidMoves = (ArrayList<ChessMove>) piece.pieceMoves(currentBoard, startPosition);

        ChessMove enPassant = enPassant(startPosition);

        if (enPassant != null) {
            //System.out.println("Adding En Passant move : " + enPassant);
            allValidMoves.add(enPassant);
        }

        for (int moveNum = 0; moveNum < allValidMoves.size(); moveNum++) {
            if (doMove(allValidMoves.get(moveNum))) {
                //System.out.println("Removing move: " + allValidMoves.get(moveNum));
                allValidMoves.remove(moveNum);
                moveNum--;
            }
        }

        //System.out.println("Size of allValidMoves: " + allValidMoves.size());

        return allValidMoves;
    }

    private boolean doMove(ChessMove move){
        if (move.isEnPassant()) {
            return testEnPassantMove(move);
        }

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

    private boolean testEnPassantMove(ChessMove move) {
        ChessBoard testBoard = currentBoard.copy();

        System.out.println(move.isEnPassant());

        testBoard.makeMove(move);
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
            //System.out.println("Wrong team's move. ");
            throw new InvalidMoveException();
        }
        if (currentBoard.getPiece(move.getStartPosition()) == null) {
            //System.out.println("No piece at position. ");
            throw new InvalidMoveException();
        }

        ArrayList<ChessMove> allValidMoves =(ArrayList<ChessMove>) validMoves(move.getStartPosition());
        boolean validMove = false;

        //System.out.println(allValidMoves.size());

        for (ChessMove testMove : allValidMoves) {
            if (testMove.equals(move)) {
                validMove = true;
                move = testMove;
                break;
            }
        }

        if (!validMove) {
            //System.out.println("Invalid move.");
            throw new InvalidMoveException();
        }

        currentBoard.makeMove(move);
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }

        if (move.isEnPassant()) {
            currentBoard.removePiece(previousMove.getEndPosition());
        }

        previousMove = move;
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
        //System.out.println("In enPassant method. ");

        ChessPiece piece = currentBoard.getPiece(startPosition);
        ChessMove newMove=null;

        if (piece == null || previousMove == null) {
            //System.out.println("No piece at position or no previous move. ");
            return null;
        }


        ChessPiece possiblePawn = currentBoard.getPiece(previousMove.getEndPosition());

        //System.out.println("Previous move: " + previousMove);

        if (possiblePawn == null) {
            //System.out.println("No piece at previous move end position. ");
            return null;
        }

        if (possiblePawn.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (abs(previousMove.getEndPosition().getRow() - previousMove.getStartPosition().getRow()) == 2) {
                if (startPosition.getCol() + 1 == previousMove.getEndPosition().getCol()) {
                    ChessPosition newPosition = new ChessPosition((abs(previousMove.getEndPosition().getRow() +
                            previousMove.getStartPosition().getRow()) / 2), previousMove.getEndPosition().getCol());
                    newMove = new ChessMove(startPosition, newPosition, null);
                    newMove.setEnPassant(true);
                } else if (startPosition.getCol() - 1 == previousMove.getEndPosition().getCol()) {
                    ChessPosition newPosition = new ChessPosition((abs(previousMove.getEndPosition().getRow() +
                            previousMove.getStartPosition().getRow()) / 2), previousMove.getEndPosition().getCol());
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

        //System.out.println("EnPassant move found: " + newMove);
        return newMove;
    }

    /*
    private ChessMove castling(ChessPosition startPosition) {
        ChessMove castle = null;
        ChessPiece piece = currentBoard.getPiece(startPosition);

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (!piece.hasPieceMoved()) {

            }
        } else {
            return null;
        }

        return castle;
    }
     */

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
