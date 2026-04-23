package com.appointment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Appointment appointment;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Ahmad", "1234", "ahmad@gmail.com");
        appointment = new Appointment(
                "2026-06-01", "10:00", 1, 2, user, AppointmentType.INDIVIDUAL
        );
    }

    @Test
    void testInitialStatusIsPending() {
        assertEquals("Pending", appointment.getStatus());
    }

    @Test
    void testConfirmChangesStatusToConfirmed() {
        appointment.confirm();
        assertEquals("Confirmed", appointment.getStatus());
    }

    @Test
    void testCancelChangesStatusToCancelled() {
        appointment.cancel();
        assertEquals("Cancelled", appointment.getStatus());
    }

    @Test
    void testGetDate() {
        assertEquals("2026-06-01", appointment.getDate());
    }

    @Test
    void testGetTime() {
        assertEquals("10:00", appointment.getTime());
    }

    @Test
    void testGetDuration() {
        assertEquals(1, appointment.getDuration());
    }

    @Test
    void testGetParticipants() {
        assertEquals(2, appointment.getParticipants());
    }

    @Test
    void testGetUser() {
        assertEquals(user, appointment.getUser());
    }

    @Test
    void testGetType() {
        assertEquals(AppointmentType.INDIVIDUAL, appointment.getType());
    }

    @Test
    void testSetType() {
        appointment.setType(AppointmentType.GROUP);
        assertEquals(AppointmentType.GROUP, appointment.getType());
    }

    @Test
    void testSetDate() {
        appointment.setDate("2026-07-01");
        assertEquals("2026-07-01", appointment.getDate());
    }

    @Test
    void testSetTime() {
        appointment.setTime("14:00");
        assertEquals("14:00", appointment.getTime());
    }

    @Test
    void testSetParticipants() {
        appointment.setParticipants(5);
        assertEquals(5, appointment.getParticipants());
    }

    @Test
    void testIsFuture_FutureDate() {
        appointment.setDate("2027-01-01");
        assertTrue(appointment.isFuture());
    }

    @Test
    void testIsFuture_PastDate() {
        appointment.setDate("2020-01-01");
        assertFalse(appointment.isFuture());
    }
}
