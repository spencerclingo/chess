package serviceTests;

import dataAccess.MemoryUserDAO;
import models.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    static MemoryUserDAO userDAO = new MemoryUserDAO();

    @BeforeAll
    public static void setUp() {
        UserService.setUserDAO(userDAO);
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
    void createUserTest() {
        UserData userData = new UserData("username", "password", "email@email");

        UserService.createUser(userData);

        assertEquals(userData, userDAO.getUser(userData));
    }

    @Test
    void userNotCreatedTest() {
        UserData userData = new UserData("username", "password", "email@email");

        UserService.createUser(userData);

        assertNotEquals(new UserData(null, null, null), userDAO.getUser(userData));
    }
}