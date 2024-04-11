package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ui.EscapeSequences.*;

public class ChessBoardPicture {

    private static final int FULL_GAME_SIZE_IN_SPACES = 10;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final String BORDER_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String BORDER_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private static final String LIGHT_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_BLUE;
    private static final String DARK_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_DARK_BLUE;
    private static final String WHITE_TEAM_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String BLACK_TEAM_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private static final String EM_SPACE = "\u2003";
    private static final String EMPTY = " " + EM_SPACE + " ";
    private static final String[] COLUMN_LABELS = {" \u2003\u2003"," h\u2003"," g\u2003"," f\u2003"," e\u2003"," d\u2003"," c\u2003"," b\u2003"," a\u2003"," \u2003\u2003"};
    private static final String[] ROW_LABELS = {"\u20031\u2003","\u20032\u2003","\u20033\u2003","\u20034\u2003","\u20035\u2003","\u20036\u2003","\u20037\u2003","\u20038\u2003"};



    public static void main(String[] args) {
        ChessGame chessGame = new ChessGame();
        chessGame.getBoard().resetBoard();
        init(chessGame.getBoard(), true, new ArrayList<>(), null);
        init(chessGame.getBoard(), false, new ArrayList<>(), null);
    }

    public static void init(ChessBoard chessBoard, boolean white, ArrayList<ChessMove> validMoves, ChessPosition startPosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        if (startPosition == null) {
            startPosition = new ChessPosition(0,0);
        }

        out.print(EscapeSequences.ERASE_SCREEN);

        drawHeader(out, white);
        drawChessBoard(out, white, chessBoard, validMoves, startPosition);
        drawHeader(out, white);

        setBlack(out);
        out.println();

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeader(PrintStream out, boolean white) {
        setBoundary(out);

        if (!white) {
            for (int colNum = 0; colNum < FULL_GAME_SIZE_IN_SPACES; colNum++) {
                out.print(COLUMN_LABELS[colNum]);
            }
        } else {
            for (int colNum = FULL_GAME_SIZE_IN_SPACES - 1; colNum >= 0; colNum--) {
                out.print(COLUMN_LABELS[colNum]);
            }
        }

        setBlack(out);
        out.println();
    }

    private static void setBoundary(PrintStream out) {
        out.print(BORDER_COLOR);
        out.print(BORDER_TEXT_COLOR);
        out.print(SET_TEXT_BOLD);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawChessBoard(PrintStream out, boolean white, ChessBoard chessBoard, ArrayList<ChessMove> validMoves, ChessPosition startPosition) {
        if (white) {
            for (int rowNum = BOARD_SIZE_IN_SQUARES; rowNum > 0; rowNum--) {
                setBoundary(out);
                out.print(ROW_LABELS[rowNum - 1]);
                for (int colNum = BOARD_SIZE_IN_SQUARES; colNum > 0; colNum--) {
                    findValidSquares(out, chessBoard, validMoves, rowNum, colNum, startPosition);
                }

                setBoundary(out);
                out.print(ROW_LABELS[rowNum - 1]);
                setBlack(out);
                out.println();
            }
        } else {
            for (int rowNum = 1; rowNum <= BOARD_SIZE_IN_SQUARES; rowNum++) {
                setBoundary(out);
                out.print(ROW_LABELS[rowNum - 1]);
                for (int colNum = 1; colNum <= BOARD_SIZE_IN_SQUARES; colNum++) {
                    findValidSquares(out, chessBoard, validMoves, rowNum, colNum, startPosition);
                }

                setBoundary(out);
                out.print(ROW_LABELS[rowNum - 1]);
                setBlack(out);
                out.println();
            }
        }
    }

    private static void findValidSquares(PrintStream out, ChessBoard chessBoard, ArrayList<ChessMove> validMoves, int rowNum, int colNum, ChessPosition startPosition) {
        boolean highlighted = false;
        boolean foundPiece = false;
        for (ChessMove move : validMoves) {
            int row = move.getEndPosition().row();
            int col = move.getEndPosition().col();

            if (row == rowNum && col == colNum) {
                highlightSquare(out, chessBoard, row, col);
                highlighted = true;
            } else if (startPosition.row() == rowNum && startPosition.col() == colNum) {
                if (!foundPiece) {
                    foundPiece = true;
                    highlightSquare(out, chessBoard, rowNum, colNum);
                    highlighted = true;
                }
            }
        }
        if (!highlighted) {
            alternatingLines(out, chessBoard, rowNum, colNum);
        }
    }

    private static void alternatingLines(PrintStream out, ChessBoard chessBoard, int rowNum, int colNum) {
        if (rowNum % 2 == 1) {
            if (colNum % 2 == 1) {
                setLightSquareColor(out);
            } else {
                setDarkSquareColor(out);
            }

            printPiece(out, chessBoard, rowNum, colNum);
        } else {
            if (colNum % 2 == 1) {
                setDarkSquareColor(out);
            } else {
                setLightSquareColor(out);
            }

            printPiece(out, chessBoard, rowNum, colNum);
        }
    }

    private static void highlightSquare(PrintStream out, ChessBoard chessBoard, int rowNum, int colNum) {
        out.print(SET_BG_COLOR_YELLOW);
        printPiece(out, chessBoard, rowNum, colNum);
    }

    private static void printPiece(PrintStream out, ChessBoard chessBoard, int rowNum, int colNum) {
        if (chessBoard.getPiece(rowNum, colNum) != null) {
            ChessPiece chessPiece = chessBoard.getPiece(rowNum, colNum);
            if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                setWhiteTeamTextColor(out);
                printWhitePiece(out, chessPiece);
            } else {
                setBlackTeamTextColor(out);
                printBlackPiece(out, chessPiece);
            }
        } else {
            out.print(EMPTY);
        }
    }

    private static void setLightSquareColor(PrintStream out) {
        out.print(LIGHT_SQUARE_COLOR);
    }

    private static void setDarkSquareColor(PrintStream out) {
        out.print(DARK_SQUARE_COLOR);
    }

    private static void setBlackTeamTextColor(PrintStream out) {
        out.print(BLACK_TEAM_TEXT_COLOR);
    }

    private static void setWhiteTeamTextColor(PrintStream out) {
        out.print(WHITE_TEAM_TEXT_COLOR);
    }

    private static void printWhitePiece(PrintStream out, ChessPiece piece) {
        pieceType(out, piece, WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING);
    }
    private static void printBlackPiece(PrintStream out, ChessPiece piece) {
        pieceType(out, piece, BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING);
    }

    private static void pieceType(PrintStream out, ChessPiece piece, String pawn, String knight, String bishop, String rook, String queen, String king) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            out.print(pawn);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            out.print(knight);
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            out.print(bishop);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            out.print(rook);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            out.print(queen);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            out.print(king);
        }
    }
}
