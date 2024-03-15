package ui;

import chess.ChessBoard;
import chess.ChessGame;

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
    private static final String[] ROW_LABELS = {"1","2","3","4","5","6","7","8"};

    public static void main(String[] args) {
        init(new ChessGame(), true);
    }

    public static void init(ChessGame chessGame, boolean white) {


        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(EscapeSequences.ERASE_SCREEN);

        drawHeader(out, white);

        //drawChessBoard(out);

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
            for (int colNum = FULL_GAME_SIZE_IN_SPACES; colNum >= 0; colNum--) {
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

    private static void drawChessBoard(PrintStream out) {
        for (int rowNum = 1; rowNum <= BOARD_SIZE_IN_SQUARES; rowNum++) {

        }
    }
}
