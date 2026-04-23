package com.appointment.rules;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GroupRuleTest {

    GroupRule rule = new GroupRule();
    User user = new User("Ahmad", "1234", "ahmad@gmail.com");

    @Test
    void testValidGroup() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 5, user, AppointmentType.GROUP);
        assertTrue(rule.isValid(a));
    }

    @Test
    void testInvalidGroup_OneParticipant() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 1, user, AppointmentType.GROUP);
        assertFalse(rule.isValid(a));
    }
}