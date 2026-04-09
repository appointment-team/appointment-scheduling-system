// FollowUpRule.java
package com.appointment.rules;
import com.appointment.domain.Appointment;

public class FollowUpRule implements BookingRuleStrategy {
    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDuration() <= 1; // Follow-up = ساعة max
    }
}