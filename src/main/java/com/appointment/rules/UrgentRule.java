// UrgentRule.java
package com.appointment.rules;
import com.appointment.domain.Appointment;

public class UrgentRule implements BookingRuleStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDuration() <= 1; // Virtual = ساعتين max
    }
}