package dataAccess;

import models.AuthData;

import java.util.HashMap;
import java.util.Objects;
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
    public AuthData getAuth(AuthData authData) {
        String authToken = authData.authToken();

        String username = reverseAuthMap.get(authToken);

        System.out.println("Username: " + username);

        if (username == null) {
            return null;
        }
        return new AuthData(authToken, username);
    }

    /**
     * @param authData containing authToken
     * @return bool of if the authToken was deleted
     */
    @Override
    public boolean deleteAuth(AuthData authData) {
        String authToken = authData.authToken();
        // This might return false if the data doesn't exist already?
        String username = reverseAuthMap.get(authToken);
        authMap.remove(username);
        reverseAuthMap.remove(authToken);
        return true;
    }

    /**
     * I think this will be unused
     *
     * @param authData containing username
     * @return bool of if authToken exists for that player
     */
    @Override
    public boolean confirmAuth(AuthData authData) {
        String username = authData.username();

        return authMap.get(username) != null;
    }

    @Override
    public boolean clear() {
        authMap.clear();
        reverseAuthMap.clear();
        return true;
    }
}
