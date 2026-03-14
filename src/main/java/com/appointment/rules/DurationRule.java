package com.appointment.rules;

import com.appointment.domain.Appointment;

public class DurationRule implements BookingRuleStrategy {

    private static final int MAX_DURATION = 2;

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDuration() <= MAX_DURATION;
    }
}