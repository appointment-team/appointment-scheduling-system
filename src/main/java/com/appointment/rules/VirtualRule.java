// VirtualRule.java
package com.appointment.rules;
import com.appointment.domain.Appointment;

public class VirtualRule implements BookingRuleStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDuration() <= 2; // max 2 hours
    }
}