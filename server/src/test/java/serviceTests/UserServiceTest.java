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

    static final MemoryUserDAO userDAO = new MemoryUserDAO();
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();

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
    void createUserTest() throws DataAccessException {
        UserData userData = new UserData("username", "password", "email@email");

        UserService.createUser(userData);

        assertEquals(userData, userDAO.getUser(userData));
    }

    @Test
    void userNotCreatedTest() {
        UserData userData = new UserData("username", "password", "email@email");

        try {
            UserService.createUser(userData);
        } catch(DataAccessException dae) {
            assertThrows(DataAccessException.class, () -> userDAO.getUser(new UserData(null, null, null)));
        }
    }

    @Test
    void loginValidUser() throws DataAccessException {
        AuthService.setAuthDAO(authDAO);

        UserData userData = new UserData("username", "password", "email@email");
        RegisterResponse registerResponse = UserService.createUser(userData);
        AuthData authData = registerResponse.authData();
        AuthService.logout(authData);

        assertEquals(200, UserService.login(userData).httpCode());
    }

    @Test
    void loginInvalidUser() throws DataAccessException {
        AuthService.setAuthDAO(authDAO);

        UserData userData = new UserData("username", "password", "email@email");
        RegisterResponse registerResponse = UserService.createUser(userData);
        AuthData authData = registerResponse.authData();
        AuthService.logout(authData);

        assertEquals(401, UserService.login(new UserData("username", "incorrect-password",null)).httpCode());
        assertEquals(401, UserService.login(new UserData("null", "null", null)).httpCode());
    }
}