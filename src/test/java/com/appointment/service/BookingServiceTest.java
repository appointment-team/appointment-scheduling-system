package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.Administrator;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import com.appointment.notifications.Observer;
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
        // ✅ Mock observer و scheduleService
        Observer mockObserver = (u, msg) -> {};
        ScheduleService scheduleService = new ScheduleService();
        bookingService = new BookingService(mockObserver, scheduleService);
        user = new User("Ahmad", "1234");
        admin = new Administrator("admin", "1234");
    }

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

    @Test
    void testCancelFutureAppointment() {

        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        bookingService.book(a);
        assertTrue(bookingService.cancelAppointment("2027-06-01", "10:00"));
    }

    @Test
    void testCancelPastAppointmentFails() {
        Appointment a = new Appointment("2020-01-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        bookingService.book(a);
        assertFalse(bookingService.cancelAppointment("2020-01-01", "10:00"));
    }

    @Test
    void testAdminCancelWithAdmin() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        bookingService.book(a);
        assertTrue(bookingService.adminCancel("2027-06-01", "10:00", admin));
    }

    @Test
    void testAdminCancelWithoutAdmin() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        bookingService.book(a);
        assertFalse(bookingService.adminCancel("2027-06-01", "10:00", null));
    }

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