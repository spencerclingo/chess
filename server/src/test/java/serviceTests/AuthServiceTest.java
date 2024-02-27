package serviceTests;

import dataAccess.MemoryAuthDAO;
import models.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    static MemoryAuthDAO authDAO = new MemoryAuthDAO();

    @BeforeAll
    static void setUp() {
        AuthService.setAuthDAO(authDAO);
    }

    @AfterEach
    void tearDown() {
        authDAO.clear();
    }

    @Test
    void getCorrectAuth() {
        AuthData authData = new AuthData(null, "username");
        AuthData newAuth  = authDAO.createAuth(authData);

        assertEquals(newAuth, AuthService.getAuth(newAuth));
    }

    @Test
    void noAuthToGet() {
        assertNull(AuthService.getAuth(new AuthData(null, "username")));
    }

    @Test
    void logoutValidUser() {
        AuthData authData = new AuthData(null, "username");
        AuthData newAuth  = authDAO.createAuth(authData);

        assertEquals(200, AuthService.logout(newAuth).httpCode());
    }

    @Test
    void logoutInvalidUser() {
        assertEquals(401, AuthService.logout(new AuthData(null, null)).httpCode());
    }

    @Test
    void clearEmptyData() {
        assertTrue(AuthService.clearData());
    }

    @Test
    void clearAddedData() {
        authDAO.createAuth(new AuthData("username", "password"));

        assertTrue(AuthService.clearData());
    }

}