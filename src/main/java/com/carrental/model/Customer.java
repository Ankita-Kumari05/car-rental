package com.carrental.model;

/** A normal website user who can book cars. Inherits everything from User. */
public class Customer extends User {

    public Customer() {
        super();
    }

    public Customer(String id, String name, String email, String password, String phone) {
        super(id, name, email, password, phone);
    }

    @Override
    public String getRole() {          // method overriding
        return "CUSTOMER";
    }
}
