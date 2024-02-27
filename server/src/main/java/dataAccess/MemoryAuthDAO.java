package dataAccess;

import models.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{

    HashMap<String, String> authMap = new HashMap<>(); // username to authToken
    HashMap<String, String> reverseAuthMap = new HashMap<>(); // authToken to username

    /**
     * @param authData containing username
     * @return full AuthData object
     */
    @Override
    public AuthData createAuth(AuthData authData) {
        String username = authData.username();

        String authToken = UUID.randomUUID().toString();
        authMap.put(username, authToken);
        reverseAuthMap.put(authToken, username);

        return new AuthData(authToken, username);
    }

    /**
     * @param authData containing authToken
     * @return full AuthData object, or null if authToken doesn't exist
     */
    @Override
    public AuthData getAuth(AuthData authData) throws DataAccessException {
        String authToken = authData.authToken();

        String username = reverseAuthMap.get(authToken);

        if (username == null) {
            throw new DataAccessException("authToken has no username");
        }
        return new AuthData(authToken, username);
    }

    /**
     * @param authData containing authToken
     */
    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        String authToken = authData.authToken();

        String username = reverseAuthMap.get(authToken);
        if (username == null) {
            throw new DataAccessException("authToken not found");
        }

        authMap.remove(username);
        reverseAuthMap.remove(authToken);
    }

    @Override
    public void clear() {
        authMap.clear();
        reverseAuthMap.clear();
    }
}
