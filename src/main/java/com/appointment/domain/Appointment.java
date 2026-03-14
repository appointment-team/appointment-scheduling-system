package com.appointment.domain;

public class Appointment {

    private String date;
    private String time;
    private int duration;
    private int participants;
    private String status;

    public Appointment(String date, String time, int duration, int participants) {
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.participants = participants;
        this.status = "Pending";
    }

    public int getDuration() {
        return duration;
    }

    public int getParticipants() {
        return participants;
    }

    public void confirm(){
        status = "Confirmed";
    }

    public String getStatus(){
        return status;
    }
}