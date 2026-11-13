package com.carrental.model;

/** A car that can be rented. Plain data class with private fields. */
public class Car {
    private String id;
    private String name;
    private String type;            // Hatchback, Sedan, SUV, MUV
    private String fuel;            // Petrol, Diesel, CNG, Electric
    private String transmission;    // Manual or Automatic
    private int seats;
    private double pricePerDay;     // in rupees
    private String color;           // hex colour such as #B3261E, used to draw the car picture
    private String image;           // optional file name inside webapp/images
    private boolean available;
    private String description;

    public Car() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFuel() { return fuel; }
    public void setFuel(String fuel) { this.fuel = fuel; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name + " (" + type + ", Rs." + pricePerDay + "/day)";
    }
}
