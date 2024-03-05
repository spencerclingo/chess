package dataAccess;

import com.google.gson.Gson;
import models.AuthData;

import java.util.UUID;
//import exception.SQLException;
//import exception.ResponseException;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{
    
    Gson gson = new Gson();

    /**
     * @param authData containing username
     *
     * @return full AuthData object
     */
    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException {
        String username = authData.username();
        String authToken = UUID.randomUUID().toString();
        AuthData authData1 = new AuthData(authToken, username);

        DatabaseManager.createAuthTable();
        
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        String json = gson.toJson(authData1);
        int id = executeUpdate(statement, authToken, username, json);
        return authData1;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException("unable to update database: %s, %s");
        }
    }

    /**
     * @param authData containing authToken
     *
     * @return full AuthData object, or null if authToken doesn't exist
     */
    @Override
    public AuthData getAuth(AuthData authData) throws DataAccessException {
        return null;
    }

    /**
     * @param authData containing authToken
     */
    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {

    }

    /**
     * Clears auth database
     */
    @Override
    public void clear() {

    }
}
