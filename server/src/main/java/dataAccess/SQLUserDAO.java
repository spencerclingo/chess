package dataAccess;

import models.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    /**
     * Clears user database
     */
    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM `chess`.`users`;";

        DatabaseManager.executeUpdate(statement);
    }

    /**
     * @param userData containing username, password, email
     */
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String username = userData.username();
        String password = userData.password();
        String email = userData.email();

        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        DatabaseManager.executeUpdate(statement, username, hashedPassword, email);
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
        try(ResultSet resultSet = DatabaseManager.executeQuery(statement, username)) {
            while (resultSet.next()) {
                passwordSQL = resultSet.getString("password");
            }
            if (passwordSQL == null) {
                System.out.println("User doesn't exist");
                throw new DataAccessException("No password attached to username");
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(password, passwordSQL);
        } catch(SQLException | DataAccessException e) {
            System.out.println(e.getMessage());
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

        String statement = "SELECT * FROM `users` WHERE `username` = ?";

        try(ResultSet resultSet = DatabaseManager.executeQuery(statement, username)) {
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
