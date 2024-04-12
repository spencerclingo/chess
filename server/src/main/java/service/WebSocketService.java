package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import models.AuthData;
import models.GameData;
import response.ClearResponse;
import response.GetGameResponse;

import java.util.Objects;

public class WebSocketService {
    private static GameDAO gameStoredDAO;
    private static AuthDAO authStoredDAO;

    public static GetGameResponse getGame(GetGameResponse getGame) {
        AuthData authData = new AuthData(getGame.authToken(), null);

        try {
            authStoredDAO.getAuth(authData);
        } catch(Exception e) {
            return new GetGameResponse(null, null, null, 401); //Unauthorized
        }

        try {
            GameData gameData = gameStoredDAO.getGame(getGame.gameData());
            return new GetGameResponse(gameData, authData.authToken(), null, 200);
        } catch(Exception e) {
            return new GetGameResponse(null, null, null,400); //Does not exist
        }
    }

    public static ClearResponse updateGame(GetGameResponse setGame) {
        AuthData authData = new AuthData(setGame.authToken(), null);

        try {
            authStoredDAO.getAuth(authData);
        } catch(Exception e) {
            return new ClearResponse( 401); //Unauthorized
        }

        try {
            gameStoredDAO.updateGame(setGame.gameData());
            return new ClearResponse(200); //Worked properly
        } catch(Exception e) {
            return new ClearResponse(400); //Game doesn't exist
        }
    }

    public static ClearResponse playerLeaves(GetGameResponse setGame) {
        GameData oldGameData;
        try {
            oldGameData = gameStoredDAO.getGame(setGame.gameData());
        } catch(Exception e) {
            System.out.println("game invalid");
            return new ClearResponse(400);
        }

        GameData newGameData;
        boolean white;
        if (setGame.username().equals(oldGameData.whiteUsername())) {
            white = true;
            newGameData = new GameData(oldGameData.gameID(), null, oldGameData.blackUsername(), oldGameData.gameName(), oldGameData.game());
        } else if (setGame.username().equals(oldGameData.blackUsername())){
            white = false;
            newGameData = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(), null, oldGameData.gameName(), oldGameData.game());
        } else {
            return new ClearResponse(200);
        }

        try {
            gameStoredDAO.removePlayer(newGameData, white);
            return new ClearResponse(200);
        } catch(Exception e) {
            System.out.println("removing player failed");
            return new ClearResponse(400);
        }
    }

    public static void setGameStoredDAO(GameDAO gameStoredDAO) {
        WebSocketService.gameStoredDAO = gameStoredDAO;
    }

    public static void setAuthStoredDAO(AuthDAO authStoredDAO) {
        WebSocketService.authStoredDAO = authStoredDAO;
    }
}
