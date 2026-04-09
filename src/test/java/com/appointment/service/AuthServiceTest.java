package com.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
    }

    @Test
    void testLoginWithValidCredentials() {
        assertTrue(authService.login("admin", "1234"));
    }

    @Test
    void testLoginWithInvalidPassword() {
        assertFalse(authService.login("admin", "wrongpass"));
    }

    @Test
    void testLoginWithInvalidUsername() {
        assertFalse(authService.login("wronguser", "1234"));
    }

    @Test
    void testLoginWithBothInvalid() {
        assertFalse(authService.login("wronguser", "wrongpass"));
    }

    @Test
    void testIsLoggedInAfterLogin() {
        authService.login("admin", "1234");
        assertTrue(authService.isLoggedIn());
    }

    @Test
    void testIsLoggedInBeforeLogin() {
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void testLogout() {
        authService.login("admin", "1234");
        authService.logout();
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void testLoginAfterLogout() {
        authService.login("admin", "1234");
        authService.logout();
        assertTrue(authService.login("admin", "1234"));
    }
}
