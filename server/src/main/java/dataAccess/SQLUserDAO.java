package dataAccess;

import models.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    /**
     * Ideally executes a SQL string
     *
     * @param statement SQL query string
     * @param params    Array list of potential objects you can pass in
     *
     * @throws DataAccessException database is unable to be updated
     */
    private void executeUpdate(String statement, Object... params) throws DataAccessException {
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
                    rs.getInt(1);
                }

            }
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException("unable to update database: %s, %s");
        }
    }

    /**
     *
     * @param statement SQL query
     * @param params Array of objects that will be inserted into your query
     * @return ResultSet of what has been selected from the database
     * @throws DataAccessException if the database doesn't accept the SQL
     */
    private ResultSet executeQuery(String statement, Object... params) throws DataAccessException {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement);

            // Set parameters if needed
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String) ps.setString(i + 1, (String) param);
                else if (param instanceof Integer) ps.setInt(i + 1, (Integer) param);

            }

            // Execute the query
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    /**
     * Clears user database
     */
    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM `chess`.`users`;";

        executeUpdate(statement);
    }

    /**
     * @param userData containing username, password, email
     */
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        String password = userData.password();
        String email = userData.email();

        String statement = "INSERT INTO `users` (username, password, email) VALUES (?, ?, ?)";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        executeUpdate(statement, username, hashedPassword, email);
    }

    /**
     * returns if the login information is valid
     *
     * @param userData containing a username and password
     *
     * @return true if user/pass match, false if they don't
     */
    @Override
    public boolean login(UserData userData) throws DataAccessException {
        String username = userData.username();
        String password = userData.password();
        String passwordSQL = null;

        String statement = "SELECT `password` FROM `users` WHERE `username` = ?";
        try(ResultSet resultSet = executeQuery(statement, username)) {
            while (resultSet.next()) {
                passwordSQL = resultSet.getString("password");
            }
            if (passwordSQL == null) {
                throw new DataAccessException("No password attached to username");
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(password, passwordSQL);
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @param userData contains username
     *
     * @return full UserData object
     */
    @Override
    public UserData getUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        String passwordSQL = null;

        String statement = "SELECT  FROM `users` WHERE `username` = ?";

        try(ResultSet resultSet = executeQuery(statement, username)) {
            while (resultSet.next()) {
                passwordSQL = resultSet.getString("password");
            }
            if (passwordSQL == null) {
                throw new DataAccessException("No password attached to username");
            }
            return new UserData(username, null, null);
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
