package com.appointment.domain;

import java.time.LocalDate;

/**
 * Represents an appointment in the scheduling system.
 * Contains all details about a scheduled meeting including
 * date, time, duration, participants, and type.
 *
 * @author YourName
 * @version 1.0
 */
public class Appointment {

    /** The date of the appointment in format YYYY-MM-DD */
    private String date;

    /** The time of the appointment in format HH:MM */
    private String time;

    /** The duration of the appointment in hours */
    private int duration;

    /** The number of participants in the appointment */
    private int participants;

    /** The current status of the appointment (Pending, Confirmed, Cancelled) */
    private String status;

    /** The user who booked the appointment */
    private User user;

    /** The type of the appointment */
    private AppointmentType type;

    /**
     * Constructs a new Appointment with the given details.
     *
     * @param date         the date of the appointment (YYYY-MM-DD)
     * @param time         the time of the appointment (HH:MM)
     * @param duration     the duration in hours (1 or 2)
     * @param participants the number of participants
     * @param user         the user booking the appointment
     * @param type         the type of the appointment
     */
    public Appointment(String date, String time, int duration,
                       int participants, User user, AppointmentType type) {
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.participants = participants;
        this.user = user;
        this.type = type;
        this.status = "Pending";
    }

    /**
     * Returns the date of the appointment.
     * @return date string in YYYY-MM-DD format
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the appointment.
     * @param date the new date in YYYY-MM-DD format
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Returns the time of the appointment.
     * @return time string in HH:MM format
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time of the appointment.
     * @param time the new time in HH:MM format
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Returns the duration of the appointment in hours.
     * @return duration in hours
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns the number of participants.
     * @return number of participants
     */
    public int getParticipants() {
        return participants;
    }

    /**
     * Sets the number of participants.
     * @param participants the new number of participants
     */
    public void setParticipants(int participants) {
        this.participants = participants;
    }

    /**
     * Returns the user who booked the appointment.
     * @return the User object
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the type of the appointment.
     * @return AppointmentType enum value
     */
    public AppointmentType getType() {
        return type;
    }

    /**
     * Sets the type of the appointment.
     * @param type the new AppointmentType
     */
    public void setType(AppointmentType type) {
        this.type = type;
    }

    /**
     * Returns the current status of the appointment.
     * @return status string (Pending, Confirmed, Cancelled)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Confirms the appointment by changing status to Confirmed.
     */
    public void confirm() {
        status = "Confirmed";
    }

    /**
     * Cancels the appointment by changing status to Cancelled.
     */
    public void cancel() {
        this.status = "Cancelled";
    }

    /**
     * Checks if the appointment is in the future.
     * @return true if appointment date is after today, false otherwise
     */
    public boolean isFuture() {
        return LocalDate.parse(this.date).isAfter(LocalDate.now());
    }
}