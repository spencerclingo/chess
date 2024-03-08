package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.SQLAuthDAO;
import models.AuthData;
import org.junit.jupiter.api.*;
import service.ServiceInitializer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    SQLAuthDAO authDAO = new SQLAuthDAO();

    @BeforeAll
    static void setup() throws DataAccessException {
        DatabaseManager.dropDatabase();
        ServiceInitializer.initialize();
    }

    @BeforeEach
    void beforeEach() throws DataAccessException {
        authDAO.clear();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        authDAO.clear();
    }

    @AfterAll
    static void finalTearDown() throws DataAccessException {
        DatabaseManager.dropDatabase();
    }

    @Test
    void noUsernameChange() throws DataAccessException {
        AuthData authData = new AuthData(null, "username");

        AuthData newAuth = authDAO.createAuth(authData);

        assertEquals(newAuth.username(), authData.username());
    }

    @Test
    void authTokenInsertedToDatabase() throws DataAccessException {
        AuthData authData = new AuthData(null, "username");
        authDAO.createAuth(authData);

        String statement = "SELECT `authToken` FROM `auth` WHERE `username` = ?;";

        try(ResultSet resultSet = DatabaseManager.executeQuery(statement, authData.username())) {
            assertTrue(resultSet.next());
        } catch(SQLException e) {
            fail();
        }
    }

    @Test
    void authTokenDoesntExist() throws DataAccessException {
        String statement = "SELECT `authToken` FROM `auth` WHERE `username` = ?;";

        try(ResultSet resultSet = DatabaseManager.executeQuery(statement, "username")) {
            assertFalse(resultSet.next());
        } catch(SQLException e) {
            fail();
        }
    }

    @Test
    void getValidAuth() throws DataAccessException {
        AuthData authData = new AuthData(null, "username");
        authData = authDAO.createAuth(authData);

        assertEquals(authData, authDAO.getAuth(authData));
    }

    @Test
    void getInvalidAuth() {
        AuthData authData = new AuthData(null, "username");

        assertThrows(DataAccessException.class, () -> authDAO.getAuth(authData));
    }

    @Test
    void deleteValidAuth() throws DataAccessException {
        AuthData authData = new AuthData(null, "username");
        authData = authDAO.createAuth(authData);

        authDAO.deleteAuth(authData);

        String statement = "DELETE FROM `auth` WHERE `authToken` = ?";
        DatabaseManager.executeUpdate(statement, authData.authToken());

        AuthData finalAuthData = authData;
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(finalAuthData));
    }

    @Test
    void deleteInvalidAuth() {
        AuthData authData = new AuthData(null, "username");

        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(authData));
    }

    @Test
    void clear() throws DataAccessException {
        AuthData authData = new AuthData(null, "username");
        authData = authDAO.createAuth(authData);

        authDAO.clear();

        AuthData finalAuthData = authData;
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(finalAuthData));
    }
}