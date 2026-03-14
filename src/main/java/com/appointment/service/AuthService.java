package com.appointment.service;

import com.appointment.domain.Administrator;

public class AuthService {

    private Administrator loggedInAdmin;

    public boolean login(String username, String password) {

        if(username.equals("admin") && password.equals("1234")){
            loggedInAdmin = new Administrator(username, password);
            return true;
        }

        return false;
    }

    public void logout(){
        loggedInAdmin = null;
    }

    public boolean isLoggedIn(){
        return loggedInAdmin != null;
    }
}