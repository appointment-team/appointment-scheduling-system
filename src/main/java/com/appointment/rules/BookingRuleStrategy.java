package com.appointment.rules;

import com.appointment.domain.Appointment;

public interface BookingRuleStrategy {
    boolean isValid(Appointment appointment);
}