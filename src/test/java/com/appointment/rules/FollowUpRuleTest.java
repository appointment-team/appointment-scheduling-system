package com.appointment.rules;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FollowUpRuleTest {

    FollowUpRule rule = new FollowUpRule();
    User user = new User("Ahmad", "1234", "ahmad@gmail.com");

    @Test
    void testValidFollowUp() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 1, user, AppointmentType.FOLLOW_UP);
        assertTrue(rule.isValid(a));
    }

    @Test
    void testInvalidFollowUp_TwoHours() {
        Appointment a = new Appointment("2026-06-01", "09:00", 2, 1, user, AppointmentType.FOLLOW_UP);
        assertFalse(rule.isValid(a));
    }
}