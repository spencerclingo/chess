package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import models.AuthData;
import models.GameData;
import response.ClearResponse;
import response.GetGameResponse;

public class WebSocketService {
    private static GameDAO gameStoredDAO;
    private static AuthDAO authStoredDAO;

    public static GetGameResponse getGame(GetGameResponse getGame) {
        AuthData authData = new AuthData(getGame.authToken(), null);

        try {
            authStoredDAO.getAuth(authData);
        } catch(Exception e) {
            return new GetGameResponse(null, null, 401); //Unauthorized
        }

        try {
            GameData gameData = gameStoredDAO.getGame(getGame.gameData());
            return new GetGameResponse(gameData, authData.authToken(), 200);
        } catch(Exception e) {
            return new GetGameResponse(null, null, 400); //Does not exist
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

    public static void setGameStoredDAO(GameDAO gameStoredDAO) {
        WebSocketService.gameStoredDAO = gameStoredDAO;
    }

    public static void setAuthStoredDAO(AuthDAO authStoredDAO) {
        WebSocketService.authStoredDAO = authStoredDAO;
    }
}
