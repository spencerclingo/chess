package dataAccess;

import models.UserData;

public interface UserDAO {
    public void clear();
    public void createUser(UserData userData);
    public boolean login(UserData userData);
    public UserData getUser(UserData userData) throws DataAccessException;
}
