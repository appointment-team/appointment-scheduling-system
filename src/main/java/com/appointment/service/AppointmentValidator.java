package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentType;
import com.appointment.rules.*;

import java.util.*;

/**
 * Validates appointments against common and type-specific booking rules.
 * Uses the Strategy Pattern to apply different rules based on appointment type.
 *
 * @author YourName
 * @version 1.0
 */
public class AppointmentValidator {

    /** List of common rules applied to all appointment types */
    private List<BookingRuleStrategy> commonRules = new ArrayList<>();

    /** Map of type-specific rules applied based on appointment type */
    private Map<AppointmentType, BookingRuleStrategy> typeRules = new HashMap<>();

    /**
     * Constructs an AppointmentValidator and initializes all booking rules.
     * Common rules: DurationRule, ParticipantLimitRule.
     * Type-specific rules: GroupRule, IndividualRule, UrgentRule, VirtualRule, FollowUpRule.
     */
    public AppointmentValidator() {
        commonRules.add(new DurationRule());
        commonRules.add(new ParticipantLimitRule());

        typeRules.put(AppointmentType.GROUP, new GroupRule());
        typeRules.put(AppointmentType.INDIVIDUAL, new IndividualRule());
        typeRules.put(AppointmentType.URGENT, new UrgentRule());
        typeRules.put(AppointmentType.VIRTUAL, new VirtualRule());
        typeRules.put(AppointmentType.FOLLOW_UP, new FollowUpRule());
    }

    /**
     * Validates an appointment against all common rules and its type-specific rule.
     * First applies common rules to all appointments, then applies
     * the rule specific to the appointment type if one exists.
     *
     * @param appointment the appointment to validate
     * @return true if all rules pass, false if any rule fails
     */
    public boolean validate(Appointment appointment) {

        for (BookingRuleStrategy rule : commonRules) {
            if (!rule.isValid(appointment)) {
                System.out.println("❌ Failed: " + rule.getClass().getSimpleName());
                return false;
            }
        }

        BookingRuleStrategy typeRule = typeRules.get(appointment.getType());

        if (typeRule != null && !typeRule.isValid(appointment)) {
            System.out.println("❌ Failed: " + typeRule.getClass().getSimpleName());
            return false;
        }

        return true;
    }
}