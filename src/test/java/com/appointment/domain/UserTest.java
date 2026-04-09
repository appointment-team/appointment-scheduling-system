package com.appointment.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGetUsername() {
        User user = new User("Ahmad", "1234");
        assertEquals("Ahmad", user.getUsername());
    }

    @Test
    void testGetPassword() {
        User user = new User("Ahmad", "1234");
        assertEquals("1234", user.getPassword());
    }

    @Test
    void testUsernameNotNull() {
        User user = new User("Ahmad", "1234");
        assertNotNull(user.getUsername());
    }

    @Test
    void testPasswordNotNull() {
        User user = new User("Ahmad", "1234");
        assertNotNull(user.getPassword());
    }

    @Test
    void testDifferentUsers() {
        User user1 = new User("Ahmad", "1234");
        User user2 = new User("Mohammed", "5678");
        assertNotEquals(user1.getUsername(), user2.getUsername());
    }
}
