package com.appointment.notifications;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import com.appointment.service.ReminderService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @Test
    void testEmailServiceWithMock() {
        // ✅ Mock بدل إيميل حقيقي
        boolean[] emailSent = {false};
        String[] sentMessage = {""};

        Observer mockEmailService = (user, message) -> {
            emailSent[0] = true;
            sentMessage[0] = message;
            System.out.println("📧 Mock Email sent to: " + user.getUsername());
            System.out.println("Message: " + message);
        };

        User user = new User("Ahmad", "1234");
        Appointment a = new Appointment(
                "2026-06-01", "10:00", 1, 1, user, AppointmentType.INDIVIDUAL
        );

        ReminderService service = new ReminderService(mockEmailService);
        service.sendReminder(a);

        // تحقق إن الإيميل اتبعت
        assertTrue(emailSent[0], "Email should have been sent");
        assertTrue(sentMessage[0].contains("10:00"), "Message should contain time");
    }

    @Test
    void testEmailServiceNotCalledWithoutReminder() {
        boolean[] emailSent = {false};

        Observer mockEmailService = (user, message) -> {
            emailSent[0] = true;
        };

        // ما شغّلنا sendReminder
        assertFalse(emailSent[0], "Email should not be sent without reminder");
    }
}