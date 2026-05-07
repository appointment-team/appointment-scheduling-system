package com.appointment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Administrator class.
 * Covers the missing branches in adminCancel() method
 * (instanceof check for both true and false cases).
 */
class AdministratorTest {

    private Administrator admin;
    private User regularUser;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        admin = new Administrator("admin", "1234");
        regularUser = new User("Ahmad", "0000", "ahmad@example.com");
        appointment = new Appointment(
                "2026-12-01", "10:00", 1, 1,
                regularUser, AppointmentType.INDIVIDUAL
        );
    }

    @Test
    void testAdminCanCancelAppointment() {
        // ✅ Branch 1: user IS an Administrator → return true
        boolean result = admin.adminCancel(appointment, admin);
        assertTrue(result, "Admin should be able to cancel the appointment");
    }

    @Test
    void testRegularUserCannotCancelAsAdmin() {
        // ✅ Branch 2: user is NOT an Administrator → return false
        boolean result = admin.adminCancel(appointment, regularUser);
        assertFalse(result, "Regular user should not be able to cancel as admin");
    }

    @Test
    void testAdminCancelChangesAppointmentStatus() {
        // Verify that admin cancel actually cancels the appointment
        admin.adminCancel(appointment, admin);
        assertEquals("Cancelled", appointment.getStatus());
    }

    @Test
    void testRegularUserDoesNotChangeAppointmentStatus() {
        // Verify that regular user's failed cancel does NOT change status
        String originalStatus = appointment.getStatus();
        admin.adminCancel(appointment, regularUser);
        assertEquals(originalStatus, appointment.getStatus());
    }

    @Test
    void testAdministratorIsAlsoAUser() {
        // Verify inheritance
        assertInstanceOf(User.class, admin);
    }

    @Test
    void testAdministratorUsername() {
        assertEquals("admin", admin.getUsername());
    }

    @Test
    void testAdministratorEmailIsDefault() {
        // The constructor sets a default email
        assertEquals("admin@system.com", admin.getEmail());
    }
}