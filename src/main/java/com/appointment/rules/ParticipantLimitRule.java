package com.appointment.rules;

import com.appointment.domain.Appointment;

public class ParticipantLimitRule implements BookingRuleStrategy {

    private static final int MAX_PARTICIPANTS = 5;

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getParticipants() <= MAX_PARTICIPANTS;
    }
}