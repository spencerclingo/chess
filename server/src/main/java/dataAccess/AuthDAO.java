package dataAccess;

import models.AuthData;

public interface AuthDAO {
    public AuthData createAuth(AuthData authData);
    public AuthData getAuth(AuthData authData) throws DataAccessException;
    public void deleteAuth(AuthData authData) throws DataAccessException;
    public void clear();
}
