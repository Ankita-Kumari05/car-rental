package com.carrental.servlet;

import com.carrental.dao.BookingDAO;
import com.carrental.dao.CarDAO;
import com.carrental.dao.UserDAO;
import com.carrental.model.Admin;
import com.carrental.model.Booking;
import com.carrental.model.Car;
import com.carrental.model.Customer;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * All admin pages. The part of the URL after /admin decides what happens:
 *   GET  /admin/dashboard        GET  /admin/cars (optional ?edit=C001)     GET  /admin/bookings
 *   POST /admin/cars/save        POST /admin/cars/delete                    POST /admin/bookings/status
 */
@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private final CarDAO carDAO = new CarDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final UserDAO userDAO = new UserDAO();

    /** True only when the logged-in user is an Admin. Otherwise redirects to the login page. */
    private boolean checkAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user instanceof Admin) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) {
            return;
        }

        String path = request.getPathInfo() == null ? "/dashboard" : request.getPathInfo();
        try {
            switch (path) {
                case "/dashboard":
                    showDashboard(request, response);
                    break;
                case "/cars":
                    showManageCars(request, response);
                    break;
                case "/bookings":
                    showManageBookings(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (DataException e) {
            throw new ServletException("Admin page error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) {
            return;
        }

        String path = request.getPathInfo() == null ? "" : request.getPathInfo();
        try {
            switch (path) {
                case "/cars/save":
                    saveCar(request, response);
                    break;
                case "/cars/delete":
                    carDAO.delete(FormUtil.get(request, "id"));
                    response.sendRedirect(request.getContextPath() + "/admin/cars?msg=deleted");
                    break;
                case "/bookings/status":
                    changeBookingStatus(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (DataException e) {
            throw new ServletException("Admin action failed: " + e.getMessage(), e);
        }
    }

    // ---------- GET handlers ----------

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws DataException, ServletException, IOException {
        List<Car> cars = carDAO.getAll();
        List<Booking> bookings = bookingDAO.getAll();

        int availableCars = 0;
        for (Car car : cars) {
            if (car.isAvailable()) {
                availableCars++;
            }
        }

        int customers = 0;
        for (User user : userDAO.getAll()) {
            if (user instanceof Customer) {
                customers++;
            }
        }

        int pending = 0;
        double approvedRevenue = 0;
        for (Booking b : bookings) {
            if (Booking.STATUS_PENDING.equals(b.getStatus())) {
                pending++;
            }
            if (Booking.STATUS_APPROVED.equals(b.getStatus())) {
                approvedRevenue += b.getTotalAmount();
            }
        }

        List<Booking> recent = new ArrayList<>(bookings);
        Collections.reverse(recent);                                   // newest first
        if (recent.size() > 5) {
            recent = recent.subList(0, 5);
        }

        request.setAttribute("totalCars", cars.size());
        request.setAttribute("availableCars", availableCars);
        request.setAttribute("totalCustomers", customers);
        request.setAttribute("totalBookings", bookings.size());
        request.setAttribute("pendingBookings", pending);
        request.setAttribute("approvedRevenue", approvedRevenue);
        request.setAttribute("recentBookings", recent);
        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }

    private void showManageCars(HttpServletRequest request, HttpServletResponse response)
            throws DataException, ServletException, IOException {
        request.setAttribute("cars", carDAO.getAll());

        String editId = FormUtil.get(request, "edit");
        if (!editId.isEmpty()) {
            request.setAttribute("editCar", carDAO.getById(editId));   // fills the form for editing
        }
        request.getRequestDispatcher("/WEB-INF/views/manage-cars.jsp").forward(request, response);
    }

    private void showManageBookings(HttpServletRequest request, HttpServletResponse response)
            throws DataException, ServletException, IOException {
        List<Booking> bookings = bookingDAO.getAll();
        Collections.reverse(bookings);                                 // newest first
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/WEB-INF/views/manage-bookings.jsp").forward(request, response);
    }

    // ---------- POST handlers ----------

    /** Adds a new car (no id in the form) or updates an existing car (id present). */
    private void saveCar(HttpServletRequest request, HttpServletResponse response)
            throws DataException, IOException {
        String ctx = request.getContextPath();
        String id = FormUtil.get(request, "id");

        Car car = id.isEmpty() ? new Car() : carDAO.getById(id);
        if (car == null) {
            response.sendRedirect(ctx + "/admin/cars?msg=invalid");
            return;
        }

        String color = FormUtil.get(request, "color");
        if (!color.matches("#[0-9A-Fa-f]{6}")) {
            color = "#2D5BA6";
        }

        car.setName(FormUtil.get(request, "name"));
        car.setType(FormUtil.get(request, "type"));
        car.setFuel(FormUtil.get(request, "fuel"));
        car.setTransmission(FormUtil.get(request, "transmission"));
        car.setSeats(FormUtil.getInt(request, "seats", 0));
        car.setPricePerDay(FormUtil.getDouble(request, "pricePerDay", 0));
        car.setColor(color);
        car.setImage(FormUtil.get(request, "image"));
        car.setAvailable("on".equals(request.getParameter("available")));   // unticked checkbox sends nothing
        car.setDescription(FormUtil.get(request, "description"));

        if (car.getName().isEmpty() || car.getSeats() < 1 || car.getPricePerDay() <= 0) {
            response.sendRedirect(ctx + "/admin/cars?msg=invalid");
            return;
        }

        if (id.isEmpty()) {
            carDAO.add(car);
        } else {
            carDAO.update(car);
        }
        response.sendRedirect(ctx + "/admin/cars?msg=saved");
    }

    private void changeBookingStatus(HttpServletRequest request, HttpServletResponse response)
            throws DataException, IOException {
        String ctx = request.getContextPath();
        String status = FormUtil.get(request, "status");
        Booking booking = bookingDAO.getById(FormUtil.get(request, "id"));

        boolean validStatus = Booking.STATUS_APPROVED.equals(status) || Booking.STATUS_REJECTED.equals(status);
        if (booking == null || !validStatus || Booking.STATUS_CANCELLED.equals(booking.getStatus())) {
            response.sendRedirect(ctx + "/admin/bookings?msg=invalid");
            return;
        }

        booking.setStatus(status);
        bookingDAO.update(booking);
        response.sendRedirect(ctx + "/admin/bookings?msg=updated");
    }
}
