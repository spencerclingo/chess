package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessBoardPicture {

    private static final int FULL_GAME_SIZE_IN_SPACES = 10;
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 3;
    private static final int BORDER_SIZE_IN_CHARS = 3;
    private static final String BORDER_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    private static final String BORDER_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private static final String LIGHT_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_LIGHT_BLUE;
    private static final String DARK_SQUARE_COLOR = EscapeSequences.SET_BG_COLOR_DARK_BLUE;
    private static final String WHITE_TEAM_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private static final String BLACK_TEAM_TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private static final String EMPTY = "   ";
    private static final String[] COLUMN_LABELS = {"   "," a "," b "," c "," d "," e "," f "," g "," h ","   "};
    private static final String[] ROW_LABELS = {" 1 "," 2 "," 3 "," 4 "," 5 "," 6 "," 7 "," 8 "};

    public static void main(String[] args) {
        init(new ChessGame(), true);
    }

    public static void init(ChessGame chessGame, boolean white) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(EscapeSequences.ERASE_SCREEN);

        drawHeader(out, white);

        drawChessBoard(out, white, chessGame.getBoard());

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeader(PrintStream out, boolean white) {
        setBoundary(out);

        if (white) {
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

    private static void drawChessBoard(PrintStream out, boolean white, ChessBoard chessBoard) {
        if (white) {
            for (int rowNum = 1; rowNum <= BOARD_SIZE_IN_SQUARES; rowNum++) {
                setBoundary(out);
                out.print(ROW_LABELS[rowNum - 1]);
                for (int colNum = 1; colNum < BOARD_SIZE_IN_SQUARES; colNum++) {

                    //This lets the rows alternate
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

                setBoundary(out);
                out.print(ROW_LABELS[rowNum - 1]);
            }
        } else {
            //TODO: put all the same things here but in reverse loop order
        }

        setBlack(out);
        out.println();
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
