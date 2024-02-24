package serviceTests;

import dataAccess.MemoryUserDAO;
import models.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    MemoryUserDAO userDAO = new MemoryUserDAO();

    @BeforeEach
    void setUp() {
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
}