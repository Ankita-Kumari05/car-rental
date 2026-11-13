package com.carrental.servlet;

import com.carrental.dao.BookingDAO;
import com.carrental.dao.CarDAO;
import com.carrental.model.Admin;
import com.carrental.model.Booking;
import com.carrental.model.Car;
import com.carrental.model.User;
import com.carrental.util.DataException;
import com.carrental.util.FormUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Customer booking pages (login required):
 *   GET  /booking?carId=C001      -> booking form for a car
 *   POST /booking                 -> save the booking, then redirect to the confirmation
 *   GET  /booking?confirmed=B1006 -> confirmation page
 *   GET  /my-bookings             -> the customer's own bookings
 *   POST /cancel-booking          -> cancel one of the customer's bookings
 */
@WebServlet({"/booking", "/my-bookings", "/cancel-booking"})
public class BookingServlet extends HttpServlet {
    private static final int MAX_DAYS = 30;

    private final CarDAO carDAO = new CarDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    /** The logged-in user from the session, or null if nobody is logged in. */
    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }
        if (user instanceof Admin) {
            response.sendRedirect(ctx + "/admin/dashboard");
            return;
        }

        try {
            if ("/my-bookings".equals(request.getServletPath())) {
                List<Booking> bookings = bookingDAO.getByUserId(user.getId());
                Collections.reverse(bookings);                  // newest first
                request.setAttribute("bookings", bookings);
                request.getRequestDispatcher("/WEB-INF/views/my-bookings.jsp").forward(request, response);
            } else {
                showBookingPage(request, response, user);
            }
        } catch (DataException e) {
            throw new ServletException("Could not load bookings: " + e.getMessage(), e);
        }
    }

    /** Shows either the confirmation (when ?confirmed=... is present) or the booking form. */
    private void showBookingPage(HttpServletRequest request, HttpServletResponse response, User user)
            throws DataException, ServletException, IOException {

        String ctx = request.getContextPath();
        String confirmedId = FormUtil.get(request, "confirmed");

        if (!confirmedId.isEmpty()) {
            Booking booking = bookingDAO.getById(confirmedId);
            if (booking == null || !booking.getUserId().equals(user.getId())) {
                response.sendRedirect(ctx + "/my-bookings");
                return;
            }
            request.setAttribute("booking", booking);
        } else {
            Car car = carDAO.getById(FormUtil.get(request, "carId"));
            if (car == null || !car.isAvailable()) {
                response.sendRedirect(ctx + "/cars");
                return;
            }
            request.setAttribute("car", car);
        }
        request.getRequestDispatcher("/WEB-INF/views/booking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ctx = request.getContextPath();
        User user = getLoggedInUser(request);
        if (user == null || user instanceof Admin) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        try {
            if ("/cancel-booking".equals(request.getServletPath())) {
                cancelBooking(request, response, user);
            } else {
                createBooking(request, response, user);
            }
        } catch (DataException e) {
            throw new ServletException("Could not save booking: " + e.getMessage(), e);
        }
    }

    private void createBooking(HttpServletRequest request, HttpServletResponse response, User user)
            throws DataException, ServletException, IOException {

        String ctx = request.getContextPath();
        Car car = carDAO.getById(FormUtil.get(request, "carId"));
        if (car == null || !car.isAvailable()) {
            response.sendRedirect(ctx + "/cars");
            return;
        }

        int days = FormUtil.getInt(request, "days", 0);
        if (days < 1 || days > MAX_DAYS) {
            request.setAttribute("error", "Enter a number of days between 1 and " + MAX_DAYS + ".");
            request.setAttribute("car", car);
            request.getRequestDispatcher("/WEB-INF/views/booking.jsp").forward(request, response);
            return;
        }

        Booking booking = new Booking(user.getId(), user.getName(), car, days);  // total is calculated inside
        bookingDAO.add(booking);                                                // booking id is generated here
        response.sendRedirect(ctx + "/booking?confirmed=" + booking.getId());
    }

    private void cancelBooking(HttpServletRequest request, HttpServletResponse response, User user)
            throws DataException, IOException {

        String ctx = request.getContextPath();
        Booking booking = bookingDAO.getById(FormUtil.get(request, "id"));

        // a customer may only cancel their own booking
        if (booking != null && booking.getUserId().equals(user.getId()) && booking.isCancellable()) {
            booking.setStatus(Booking.STATUS_CANCELLED);
            bookingDAO.update(booking);
            response.sendRedirect(ctx + "/my-bookings?msg=cancelled");
        } else {
            response.sendRedirect(ctx + "/my-bookings?msg=invalid");
        }
    }
}
