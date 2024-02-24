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
    void authCreated() {
        AuthData authData = new AuthData(null, "username");
        AuthData newAuth = AuthService.createAuth(authData);

        assertTrue(authDAO.confirmAuth(new AuthData(null, "username")));
        assertEquals(newAuth.username(), authDAO.getAuth(new AuthData(newAuth.authToken(), null)).username());
    }

    @Test
    void authNotCreated() {
        AuthData authData = new AuthData("12345", "username");

        assertFalse(authDAO.confirmAuth(new AuthData(null, "username")));
        assertNotEquals(authData, authDAO.getAuth(new AuthData("12345", null)));
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

        assertTrue(AuthService.logout(newAuth));
    }

    @Test
    void logoutInvalidUser() {
        assertFalse(AuthService.logout(new AuthData(null, null)));
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