package serviceTests;

import dataAccess.MemoryUserDAO;
import models.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserMemoryDAOTest {

    static MemoryUserDAO userDAO = new MemoryUserDAO();

    @BeforeAll
    public static void setUp() {
        UserService.setUserDAO(userDAO);
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
}