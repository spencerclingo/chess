package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import models.AuthData;
import models.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import response.RegisterResponse;
import service.AuthService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    static MemoryUserDAO userDAO = new MemoryUserDAO();
    static MemoryAuthDAO authDAO = new MemoryAuthDAO();

    @BeforeAll
    public static void setUp() {
        UserService.setUserDAO(userDAO);
        UserService.setAuthDAO(authDAO);
    }

    @AfterEach
    void tearDown() {
        userDAO.clear();
    }

    @Test
    void clearEmptyData() {
        assertTrue(UserService.clearData());
    }

    @Test
    void clearAddedData() {
        userDAO.createUser(new UserData("username", "password", "email@email"));

        assertTrue(UserService.clearData());
    }

    @Test
    void userExists() {
        UserData userData = new UserData("username", "password", "email@email");
        userDAO.createUser(userData);

        assertTrue(UserService.getUser(userData));
    }

    @Test
    void userDoesNotExist() {
        UserData userData = new UserData("username", "password", "email@email");

        assertFalse(UserService.getUser(userData));
    }

    @Test
    void createUserTest() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email@email");

        UserService.createUser(userData);

        assertEquals(userData, userDAO.getUser(userData));
    }

    @Test
    void userNotCreatedTest() {
        UserData userData = new UserData("username", "password", "email@email");

        UserService.createUser(userData);

        assertThrows(DataAccessException.class, () -> userDAO.getUser(new UserData(null, null, null)));
    }

    @Test
    void loginValidUser() {
        AuthService.setAuthDAO(authDAO);

        UserData userData = new UserData("username", "password", "email@email");
        RegisterResponse registerResponse = UserService.createUser(userData);
        AuthData authData = registerResponse.authData();
        AuthService.logout(authData);

        assertEquals(200, UserService.login(userData).HTTPCode());
    }

    @Test
    void loginInvalidUser() {
        AuthService.setAuthDAO(authDAO);

        UserData userData = new UserData("username", "password", "email@email");
        RegisterResponse registerResponse = UserService.createUser(userData);
        AuthData authData = registerResponse.authData();
        AuthService.logout(authData);

        assertEquals(401, UserService.login(new UserData("username", "incorrect-password",null)).HTTPCode());
        assertEquals(401, UserService.login(new UserData("null", "null", null)).HTTPCode());
    }
}