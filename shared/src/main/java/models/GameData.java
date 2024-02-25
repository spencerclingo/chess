package models;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData copyChangedID(int newGameID){
        return new GameData(newGameID, this.whiteUsername, this.blackUsername, this.gameName, this.game);
    }
}
