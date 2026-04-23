package com.appointment.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGetUsername() {
        User user = new User("Ahmad", "1234", "ahmad@gmail.com");
        assertEquals("Ahmad", user.getUsername());
    }

    @Test
    void testGetPassword() {
        User user = new User("Ahmad", "1234", "ahmad@gmail.com");
        assertEquals("1234", user.getPassword());
    }

    @Test
    void testGetEmail() {
        User user = new User("Ahmad", "1234", "ahmad@gmail.com");
        assertEquals("ahmad@gmail.com", user.getEmail());
    }

    @Test
    void testUsernameNotNull() {
        User user = new User("Ahmad", "1234", "ahmad@gmail.com");
        assertNotNull(user.getUsername());
    }

    @Test
    void testPasswordNotNull() {
        User user = new User("Ahmad", "1234", "ahmad@gmail.com");
        assertNotNull(user.getPassword());
    }

    @Test
    void testDifferentUsers() {
        User user1 = new User("Ahmad", "1234", "ahmad@gmail.com");
        User user2 = new User("Mohammed", "5678", "mohammed@gmail.com");
        assertNotEquals(user1.getUsername(), user2.getUsername());
    }
}