package com.appointment;

import com.appointment.service.AuthService;
import com.appointment.service.ScheduleService;
import com.appointment.domain.Appointment;
import com.appointment.rules.DurationRule;
import com.appointment.rules.ParticipantLimitRule;
import com.appointment.service.BookingService;
public class Main {

    public static void main(String[] args) {

        BookingService bookingService = new BookingService();

        bookingService.addRule(new DurationRule());
        bookingService.addRule(new ParticipantLimitRule());

        Appointment appointment = new Appointment(
                "2026-05-10",
                "10:00",
                2,
                3
        );

        boolean result = bookingService.book(appointment);

        if(result){
            System.out.println("Appointment booked successfully");
        }else{
            System.out.println("Booking failed بسبب مخالفة القواعد");
        }
    }
}