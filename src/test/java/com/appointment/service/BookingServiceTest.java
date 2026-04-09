package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.Administrator;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import com.appointment.rules.DurationRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private BookingService bookingService;
    private User user;
    private Administrator admin;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService();
        user = new User("Ahmad", "1234");
        admin = new Administrator("admin", "1234");
    }

    // ✅ Booking Tests
    @Test
    void testBookValidAppointment() {
        Appointment a = new Appointment("2026-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertTrue(bookingService.book(a));
        assertEquals("Confirmed", a.getStatus());
    }

    @Test
    void testBookFailsWithRule() {
        bookingService.addRule(new DurationRule());
        Appointment a = new Appointment("2026-06-01", "10:00", 5, 2, user, AppointmentType.GROUP);
        assertFalse(bookingService.book(a));
    }

    // ✅ Modify Tests
    @Test
    void testModifyFutureAppointment() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertTrue(bookingService.modifyAppointment(a, "2027-07-01", "11:00"));
        assertEquals("2027-07-01", a.getDate());
        assertEquals("11:00", a.getTime());
    }

    @Test
    void testModifyPastAppointmentFails() {
        Appointment a = new Appointment("2020-01-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertFalse(bookingService.modifyAppointment(a, "2020-02-01", "11:00"));
    }

    // ✅ Cancel Tests
    @Test
    void testCancelFutureAppointment() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertTrue(bookingService.cancelAppointment(a));
        assertEquals("Cancelled", a.getStatus());
        assertEquals(0, a.getParticipants());
    }

    @Test
    void testCancelPastAppointmentFails() {
        Appointment a = new Appointment("2020-01-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertFalse(bookingService.cancelAppointment(a));
    }

    // ✅ Admin Cancel Tests
    @Test
    void testAdminCancelWithAdmin() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertTrue(bookingService.adminCancel(a, admin));
        assertEquals("Cancelled", a.getStatus());
    }

    @Test
    void testAdminCancelWithoutAdmin() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertFalse(bookingService.adminCancel(a, null));
    }

    // ✅ Admin Modify Tests
    @Test
    void testAdminModifyWithAdmin() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertTrue(bookingService.adminModify(a, admin, "2027-08-01", "12:00"));
        assertEquals("2027-08-01", a.getDate());
        assertEquals("12:00", a.getTime());
    }

    @Test
    void testAdminModifyWithoutAdmin() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        assertFalse(bookingService.adminModify(a, null, "2027-08-01", "12:00"));
    }
}