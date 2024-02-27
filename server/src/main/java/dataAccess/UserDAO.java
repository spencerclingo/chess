package dataAccess;

import models.UserData;

public interface UserDAO {
    void clear();
    void createUser(UserData userData);
    boolean login(UserData userData);
    UserData getUser(UserData userData) throws DataAccessException;
}
