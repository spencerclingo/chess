package dataAccess;

import models.UserData;

public interface UserDAO {
    public boolean clear();
    public boolean createUser(UserData userData);
    public UserData getUser(String username);
}
