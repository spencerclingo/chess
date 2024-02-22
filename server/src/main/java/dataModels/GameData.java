package dataModels;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        this.gameID=gameID;
        this.whiteUsername=whiteUsername;
        this.blackUsername=blackUsername;
        this.gameName=gameName;
        this.game=game;
    }

    @Override
    public int gameID() {
        return gameID;
    }

    @Override
    public String whiteUsername() {
        return whiteUsername;
    }

    @Override
    public String blackUsername() {
        return blackUsername;
    }

    @Override
    public String gameName() {
        return gameName;
    }

    @Override
    public ChessGame game() {
        return game;
    }
}
