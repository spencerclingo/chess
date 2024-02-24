package dataAccess;

import models.AuthData;

public interface AuthDAO {
    public AuthData createAuth(AuthData authData);
    public AuthData getAuth(AuthData authData);
    public boolean deleteAuth(AuthData authData);
    public boolean confirmAuth(AuthData authData);
    public boolean clear();
}
