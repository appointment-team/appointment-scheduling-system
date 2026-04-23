package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentValidatorTest {

    AppointmentValidator validator = new AppointmentValidator();
    User user = new User("Ahmad", "1234", "ahmad@gmail.com");

    @Test
    void testValidAppointment() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 5, user, AppointmentType.GROUP);
        assertTrue(validator.validate(a));
    }

    @Test
    void testInvalidAppointment_GroupOneParticipant() {
        Appointment a = new Appointment("2026-06-01", "09:00", 1, 1, user, AppointmentType.GROUP);
        assertFalse(validator.validate(a));
    }

    @Test
    void testInvalidAppointment_ZeroDuration() {
        Appointment a = new Appointment("2026-06-01", "09:00", 0, 1, user, AppointmentType.INDIVIDUAL);
        assertFalse(validator.validate(a));
    }
}