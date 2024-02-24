package dataAccess;

import models.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    HashMap<String, UserData> userMap = new HashMap<>();

    /**
     * @param userData containing username, password, email
     * @return bool of success
     */
    @Override
    public boolean createUser(UserData userData) {
        String username = userData.username();
        userMap.put(username, userData);
        return true;
    }

    /**
     * @param userData contains username
     * @return full UserData object
     */
    @Override
    public UserData getUser(UserData userData) {
        String username = userData.username();

        return userMap.get(username);
    }

    @Override
    public boolean clear() {
        userMap.clear();
        return true;
    }
}
