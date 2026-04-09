package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.Administrator;
import com.appointment.rules.BookingRuleStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing appointment bookings.
 * Handles booking, modification, and cancellation of appointments
 * for both users and administrators.
 *
 * @author YourName
 * @version 1.0
 */
public class BookingService {

    /** List of booking rules to validate appointments */
    private List<BookingRuleStrategy> rules = new ArrayList<>();

    /** List of all booked appointments in memory */
    private List<Appointment> appointments = new ArrayList<>();

    /**
     * Adds a booking rule to the validation chain.
     *
     * @param rule the BookingRuleStrategy to add
     */
    public void addRule(BookingRuleStrategy rule) {
        rules.add(rule);
    }

    /**
     * Books an appointment after validating all rules.
     * If all rules pass, the appointment is confirmed and saved.
     *
     * @param appointment the appointment to book
     * @return true if booking succeeded, false if any rule failed
     */
    public boolean book(Appointment appointment) {
        for (BookingRuleStrategy rule : rules) {
            if (!rule.isValid(appointment)) {
                return false;
            }
        }
        appointment.confirm();
        appointments.add(appointment);
        return true;
    }

    /**
     * Modifies an existing appointment by updating its date and time.
     * Only future appointments can be modified.
     *
     * @param appointment the appointment to modify
     * @param newDate     the new date in YYYY-MM-DD format
     * @param newTime     the new time in HH:MM format
     * @return true if modification succeeded, false if appointment is in the past
     */
    public boolean modifyAppointment(Appointment appointment, String newDate, String newTime) {
        if (!appointment.isFuture()) {
            System.out.println("❌ Cannot modify past appointments");
            return false;
        }
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        System.out.println("✅ Appointment modified");
        return true;
    }

    /**
     * Cancels an existing appointment by a user.
     * Only future appointments can be cancelled.
     * Resets participants count to 0 after cancellation.
     *
     * @param appointment the appointment to cancel
     * @return true if cancellation succeeded, false if appointment is in the past
     */
    public boolean cancelAppointment(Appointment appointment) {
        if (!appointment.isFuture()) {
            System.out.println("❌ Cannot cancel past appointments");
            return false;
        }
        appointment.cancel();
        appointment.setParticipants(0);
        System.out.println("✅ Appointment cancelled");
        return true;
    }

    /**
     * Cancels any appointment by an administrator.
     * Administrators can cancel any appointment regardless of date.
     *
     * @param appointment the appointment to cancel
     * @param admin       the administrator performing the cancellation
     * @return true if cancellation succeeded, false if admin is null
     */
    public boolean adminCancel(Appointment appointment, Administrator admin) {
        if (admin == null) {
            System.out.println("❌ Only admin can cancel");
            return false;
        }
        appointment.cancel();
        appointment.setParticipants(0);
        System.out.println("✅ Admin cancelled appointment");
        return true;
    }

    /**
     * Modifies any appointment by an administrator.
     * Administrators can modify any appointment regardless of date.
     *
     * @param appointment the appointment to modify
     * @param admin       the administrator performing the modification
     * @param newDate     the new date in YYYY-MM-DD format
     * @param newTime     the new time in HH:MM format
     * @return true if modification succeeded, false if admin is null
     */
    public boolean adminModify(Appointment appointment, Administrator admin,
                               String newDate, String newTime) {
        if (admin == null) {
            System.out.println("❌ Only admin can modify");
            return false;
        }
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        System.out.println("✅ Admin modified appointment");
        return true;
    }
}