package com.carrental.model;

/**
 * Abstract base class for everyone who can log in.
 * Fields are private (encapsulation) and accessed through getters/setters.
 * Each subclass must say what its role is (abstraction).
 */
public abstract class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;

    public User() {
    }

    public User(String id, String name, String email, String password, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    /** Every subclass has to provide its own role ("ADMIN" or "CUSTOMER"). */
    public abstract String getRole();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return getRole() + ": " + name + " (" + email + ")";
    }
}
