package dataAccess;

import models.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.ServiceInitializer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    SQLUserDAO userDAO = new SQLUserDAO();

    @BeforeAll
    static void setup() throws DataAccessException {
        ServiceInitializer.initialize();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    void clear() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email@email");
        userDAO.createUser(userData);

        userDAO.clear();

        assertThrows(DataAccessException.class, () -> userDAO.getUser(userData));
    }

    @Test
    void createValidUser() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email@email");
        userDAO.createUser(userData);

        String statement = "SELECT * FROM `users` WHERE `username` = ?";

        try (ResultSet resultSet = DatabaseManager.executeQuery(statement, userData.username())) {
            if (resultSet.next()) {
                assertEquals("username", resultSet.getString("username"));
                assertEquals("email@email", resultSet.getString("email"));
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                assertTrue(encoder.matches("password", resultSet.getString("password")));
            } else {
                fail();
            }
        } catch(SQLException e) {
            fail();
        }
    }

    @Test
    void createInvalidUser() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email@email");
        userDAO.createUser(userData);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(userData));
    }

    @Test
    void loginValidUser() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email@email");
        userDAO.createUser(userData);

        assertTrue(userDAO.login(userData));
    }
    @Test
    void loginInvalidUser() {
        UserData userData = new UserData("username", "password", "email@email");

        assertThrows(DataAccessException.class, () -> userDAO.login(userData));
    }

    @Test
    void getValidUser() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email@email");
        userDAO.createUser(userData);

        assertEquals(userData.username(), userDAO.getUser(userData).username());
    }

    @Test
    void getInvalidUser() {
        UserData userData = new UserData("username", "password", "email@email");

        assertThrows(DataAccessException.class, () -> userDAO.getUser(userData));
    }
}