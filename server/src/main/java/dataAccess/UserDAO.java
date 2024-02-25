package dataAccess;

import models.UserData;

public interface UserDAO {
    public boolean clear();
    public boolean createUser(UserData userData);
    public boolean login(UserData userData);
    public UserData getUser(UserData userData) throws DataAccessException;
}
