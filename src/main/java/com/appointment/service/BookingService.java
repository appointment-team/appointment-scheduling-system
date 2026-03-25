package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.rules.BookingRuleStrategy;

import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private List<BookingRuleStrategy> rules = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();

    public void addRule(BookingRuleStrategy rule){
        rules.add(rule);
    }

    public boolean book(Appointment appointment){

        for(BookingRuleStrategy rule : rules){
            if(!rule.isValid(appointment)){
                return false;
            }
        }

        appointment.confirm();
        appointments.add(appointment);

        return true;
    }

    public boolean modifyAppointment(Appointment appointment, String newTime){

        if(!appointment.isFuture()){
            System.out.println("Cannot modify past appointments");
            return false;
        }

        appointment.setTime(newTime);

        System.out.println("Appointment modified");

        return true;
    }
    public boolean cancelAppointment(Appointment appointment){

        if(!appointment.isFuture()){
            System.out.println("Cannot cancel past appointments");
            return false;
        }

        appointment.cancel();

        System.out.println("Appointment cancelled");

        return true;
    }




}