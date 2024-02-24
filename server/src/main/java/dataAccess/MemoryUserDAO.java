package dataAccess;

import models.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    HashMap<String, UserData> userMap = new HashMap<>();

    @Override
    public boolean clear() {
        userMap.clear();
        return true;
    }

    @Override
    public boolean createUser(UserData userData) {
        String username = userData.username();
        userMap.put(username, userData);
        return true;
    }

    @Override
    public UserData getUser(String username) {
        return userMap.get(username);
    }
}
