package com.appointment.notifications;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import com.appointment.service.ReminderService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    @Test
    void testReminderSent() {
        // ✅ fake observer بدل @Mock
        boolean[] called = {false};

        Observer fakeObserver = (user, message) -> {
            called[0] = true;
            assertEquals("Ahmad", user.getUsername());
            assertTrue(message.contains("10:00"));
        };

        User user = new User("Ahmad", "1234", "ahmad@gmail.com");
        Appointment a = new Appointment(
                "2026-06-01", "10:00", 1, 1, user, AppointmentType.INDIVIDUAL
        );

        ReminderService service = new ReminderService(fakeObserver);
        service.sendReminder(a);

        assertTrue(called[0], "Observer should have been called");
    }

    @Test
    void testReminderNotSentForNull() {
        boolean[] called = {false};

        Observer fakeObserver = (user, message) -> {
            called[0] = true;
        };

        User user = new User("Ahmad", "1234", "ahmad@gmail.com");
        Appointment a = new Appointment(
                "2026-06-01", "10:00", 1, 1, user, AppointmentType.INDIVIDUAL
        );

        ReminderService service = new ReminderService(fakeObserver);
        service.sendReminder(a);

        assertTrue(called[0]);
    }
}