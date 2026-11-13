package com.carrental.model;

/** The administrator who manages cars and bookings. */
public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(String id, String name, String email, String password, String phone) {
        super(id, name, email, password, phone);
    }

    @Override
    public String getRole() {          // method overriding
        return "ADMIN";
    }

    @Override
    public String toString() {         // overriding toString as well
        return "Administrator " + getName();
    }
}
