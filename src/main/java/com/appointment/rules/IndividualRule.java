// IndividualRule.java
package com.appointment.rules;
import com.appointment.domain.Appointment;

public class IndividualRule implements BookingRuleStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getParticipants() == 1; // Individual = مشارك واحد فقط
    }
}