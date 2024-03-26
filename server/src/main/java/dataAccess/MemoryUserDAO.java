package dataAccess;

import models.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    final HashMap<String, UserData> userMap = new HashMap<>();

    /**
     * @param userData containing username, password, email
     */
    @Override
    public void createUser(UserData userData) {
        String username = userData.username();
        userMap.put(username, userData);
    }

    /**
     * @param userData contains username
     * @return full UserData object
     */
    @Override
    public UserData getUser(UserData userData) throws DataAccessException {
        String username = userData.username();

        if (userMap.get(username) == null) {
            throw new DataAccessException("No user with this username found");
        }

        return userMap.get(username);
    }

    /**
     * returns if the login information is valid
     *
     * @param userData containing a username and password
     * @return true if user/pass match, false if they don't
     */
    @Override
    public boolean login(UserData userData) {
        String username = userData.username();
        String password = userData.password();

        UserData storedData = userMap.get(username);
        return storedData.password().equals(password);
    }

    /**
     * Clears user database
     */
    @Override
    public void clear() {
        userMap.clear();
    }
}
