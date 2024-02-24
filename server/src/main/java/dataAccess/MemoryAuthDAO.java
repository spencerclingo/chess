package dataAccess;

import models.AuthData;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{

    HashMap<String, String> authMap = new HashMap<>(); // username to authToken
    HashMap<String, String> reverseAuthMap = new HashMap<>(); // authToken to username

    @Override
    public AuthData createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        authMap.put(username, authToken);
        reverseAuthMap.put(authToken, username);
        return newAuth;
    }

    @Override
    public AuthData getAuth(String authToken) {
        String username = reverseAuthMap.get(authToken);
        if (username.isEmpty()) {
            return null;
        }
        return new AuthData(authToken, username);
    }

    @Override
    public boolean deleteAuth(AuthData authData) {
        // This might return false if the data doesn't exist already?
        authMap.remove(authData.username());
        reverseAuthMap.remove(authData.authToken());
        return true;
    }

    @Override
    public boolean confirmAuth(String username) {
        return authMap.get(username) != null;
    }

    @Override
    public boolean clear() {
        authMap.clear();
        reverseAuthMap.clear();
        return true;
    }
}
