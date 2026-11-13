package com.carrental.model;

import java.time.LocalDate;

/**
 * One car booking. The customer and car names are copied into the booking,
 * so the booking still reads correctly even if the car is edited later.
 */
public class Booking {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String id;
    private String userId;
    private String customerName;
    private String carId;
    private String carName;
    private int days;
    private double pricePerDay;
    private double totalAmount;
    private String status;
    private String bookingDate;

    public Booking() {
    }

    /** Creates a new booking for a customer. The total is calculated here. */
    public Booking(String userId, String customerName, Car car, int days) {
        this.userId = userId;
        this.customerName = customerName;
        this.carId = car.getId();
        this.carName = car.getName();
        this.days = days;
        this.pricePerDay = car.getPricePerDay();
        this.totalAmount = calculateTotal();
        this.status = STATUS_PENDING;
        this.bookingDate = LocalDate.now().toString();
    }

    /** Total Amount = Rental Days x Price Per Day */
    public double calculateTotal() {
        return days * pricePerDay;
    }

    /** A customer may cancel only while the booking is Pending or Approved. */
    public boolean isCancellable() {
        return STATUS_PENDING.equals(status) || STATUS_APPROVED.equals(status);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    @Override
    public String toString() {
        return id + ": " + customerName + " booked " + carName + " for " + days + " day(s), " + status;
    }
}
