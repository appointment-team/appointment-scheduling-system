package com.appointment.rules;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IndividualRuleTest {

    IndividualRule rule = new IndividualRule();
    User user = new User("Ahmad", "1234", "ahmad@gmail.com");

    @Test
    void testValidIndividual() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 1, user, AppointmentType.INDIVIDUAL);
        assertTrue(rule.isValid(a));
    }

    @Test
    void testInvalidIndividual_MultipleParticipants() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 3, user, AppointmentType.INDIVIDUAL);
        assertFalse(rule.isValid(a));
    }
}