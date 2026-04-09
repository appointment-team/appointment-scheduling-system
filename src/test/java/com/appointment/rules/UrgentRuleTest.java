package com.appointment.rules;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UrgentRuleTest {

    UrgentRule rule = new UrgentRule();
    User user = new User("Ahmad", "1234");

    @Test
    void testValidUrgent() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 1, user, AppointmentType.URGENT);
        assertTrue(rule.isValid(a));
    }

    @Test
    void testInvalidUrgent_TwoHours() {
        Appointment a = new Appointment("2026-06-01", "09:00", 2, 1, user, AppointmentType.URGENT);
        assertFalse(rule.isValid(a));
    }
}