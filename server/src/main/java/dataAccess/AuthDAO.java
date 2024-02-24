package dataAccess;

import models.AuthData;

public interface AuthDAO {
    public AuthData createAuth(String username);
    public AuthData getAuth(String authToken);
    public boolean deleteAuth(AuthData authData); //Maybe this takes in a string type?
    public boolean confirmAuth(String username);
    public boolean clear();
}
