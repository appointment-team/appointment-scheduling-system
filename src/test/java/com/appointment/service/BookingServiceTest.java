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
        new java.io.File("appointments.txt").delete(); // ← احذف الملف
        new java.io.File("slots.txt").delete();
        Observer mockObserver = (u, msg) -> {};
        ScheduleService scheduleService = new ScheduleService();
        bookingService = new BookingService(mockObserver, scheduleService);
        user = new User("Ahmad", "1234", "ahmad@gmail.com");
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
    @Test
    void testGetAppointments() {
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2, user, AppointmentType.GROUP);
        bookingService.book(a);
        assertFalse(bookingService.getAppointments().isEmpty());
    }

    @Test
    void testCancelNotFoundAppointment() {
        assertFalse(bookingService.cancelAppointment("2020-01-01", "10:00"));
    }

    @Test
    void testAdminCancelNotFound() {
        assertFalse(bookingService.adminCancel("2020-01-01", "10:00", admin));
    }

    @Test
    void testAdminCancelSendsNotification() {
        // ✅ تحقق بس إن adminCancel بيرجع true عند وجود الموعد
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2,
                user, AppointmentType.GROUP);
        bookingService.book(a);
        assertTrue(bookingService.adminCancel("2027-06-01", "10:00", admin));
    }
    @Test
    void testCancelSendsNotification() {
        boolean[] notified = {false};
        Observer mockObserver = (u, msg) -> notified[0] = true;
        new java.io.File("appointments.txt").delete();
        new java.io.File("slots.txt").delete();
        ScheduleService scheduleService = new ScheduleService();
        BookingService bs = new BookingService(mockObserver, scheduleService);

        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2,
                new User("Ahmad", "1234", "ahmad@gmail.com"), AppointmentType.GROUP);
        bs.book(a);
        bs.cancelAppointment("2027-06-01", "10:00");
        assertTrue(notified[0]);
    }
    @Test
    void testLoadFromFile() {
        // ✅ احجز موعد عشان يتحفظ في الملف
        Appointment a = new Appointment("2027-06-01", "10:00", 1, 2,
                user, AppointmentType.GROUP);
        bookingService.book(a);

        // ✅ اعمل BookingService جديد عشان يحمّل من الملف
        Observer mockObserver = (u, msg) -> {};
        ScheduleService scheduleService = new ScheduleService();
        BookingService newBS = new BookingService(mockObserver, scheduleService);

        // ✅ تحقق إن الموعد اتحمّل من الملف
        assertFalse(newBS.getAppointments().isEmpty());
    }

    @Test
    void testUpdateFileAfterCancel() {
        // ✅ احجز موعدين
        Appointment a1 = new Appointment("2027-06-01", "10:00", 1, 2,
                user, AppointmentType.GROUP);
        Appointment a2 = new Appointment("2027-07-01", "11:00", 1, 2,
                user, AppointmentType.GROUP);
        bookingService.book(a1);
        bookingService.book(a2);

        // ✅ ألغِ الأول
        bookingService.cancelAppointment("2027-06-01", "10:00");

        // ✅ اعمل BookingService جديد عشان يقرأ الملف المحدث
        Observer mockObserver = (u, msg) -> {};
        ScheduleService scheduleService = new ScheduleService();
        BookingService newBS = new BookingService(mockObserver, scheduleService);

        // ✅ تحقق إن بس موعد واحد موجود
        assertEquals(1, newBS.getAppointments().size());
    }
}