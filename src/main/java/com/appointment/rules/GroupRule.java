package com.appointment.rules;

import com.appointment.domain.Appointment;

public class GroupRule implements BookingRuleStrategy {

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getParticipants() > 1; // Group = أكثر من مشارك
    }
}