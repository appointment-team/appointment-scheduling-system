package com.appointment.domain;

public class Administrator extends User {

    public Administrator(String username, String password) {
        super(username, password, "admin@system.com"); // ← أضف إيميل افتراضي للأدمن
    }

    public boolean adminCancel(Appointment appointment, User user) {
        if (!(user instanceof Administrator)) {
            System.out.println("Only admin can perform this action");
            return false;
        }
        appointment.cancel();
        System.out.println("Admin cancelled the appointment");
        return true;
    }
}