package com.appointment.rules;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DurationRuleTest {

    DurationRule rule = new DurationRule();
    User user = new User("Ahmad", "1234", "ahmad@gmail.com");

    @Test
    void testValidDuration() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 1, user, AppointmentType.URGENT);
        assertTrue(rule.isValid(a));
    }

    @Test
    void testInvalidDuration_Zero() {
        Appointment a = new Appointment("2026-06-01", "09:00", 0, 1, user, AppointmentType.URGENT);
        assertFalse(rule.isValid(a));
    }

    @Test
    void testInvalidDuration_TooLong() {
        Appointment a = new Appointment("2026-06-01", "09:00", 5, 1, user, AppointmentType.URGENT);
        assertFalse(rule.isValid(a));
    }
}