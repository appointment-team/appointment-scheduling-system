package com.appointment.domain;
import java.time.LocalDate;

public class Appointment {

    private String date;
    private String time;
    private int duration;
    private int participants;
    private String status;
    private User user;

    public Appointment(String date, String time, int duration, int participants, User user) {
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.participants = participants;
        this.user = user;
        this.status = "Pending";
    }

    public String getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }

    public int getParticipants() {
        return participants;
    }

    public User getUser() {
        return user;
    }

    public void confirm(){
        status = "Confirmed";
    }

    public String getStatus(){
        return status;


    }

    public void cancel(){
        status = "Cancelled";
    }
    public boolean isFuture() {
        LocalDate appointmentDate = LocalDate.parse(date);
        return appointmentDate.isAfter(LocalDate.now());
    }
    public void setTime(String time){
        this.time = time;
    }

}
