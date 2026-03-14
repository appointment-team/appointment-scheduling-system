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
}