package com.carrental.servlet;

import com.carrental.dao.CarDAO;
import com.carrental.model.Car;
import com.carrental.util.DataException;
import com.carrental.util.FormUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Public pages about cars:
 *   /home         -> home page with three featured cars
 *   /cars         -> list of available cars (optional ?type=SUV filter)
 *   /car-details  -> details of one car (?id=C001)
 */
@WebServlet({"/home", "/cars", "/car-details"})
public class CarServlet extends HttpServlet {
    private final CarDAO carDAO = new CarDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            switch (request.getServletPath()) {
                case "/home":
                    showHome(request, response);
                    break;
                case "/cars":
                    showCars(request, response);
                    break;
                default:
                    showDetails(request, response);
            }
        } catch (DataException e) {
            throw new ServletException("Could not load cars: " + e.getMessage(), e);
        }
    }

    private void showHome(HttpServletRequest request, HttpServletResponse response)
            throws DataException, ServletException, IOException {
        List<Car> available = carDAO.getAvailable();
        List<Car> featured = available.size() > 3 ? available.subList(0, 3) : available;
        request.setAttribute("featuredCars", featured);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private void showCars(HttpServletRequest request, HttpServletResponse response)
            throws DataException, ServletException, IOException {
        String type = FormUtil.get(request, "type");

        List<Car> cars = new ArrayList<>();
        for (Car car : carDAO.getAvailable()) {
            if (type.isEmpty() || car.getType().equalsIgnoreCase(type)) {
                cars.add(car);
            }
        }
        request.setAttribute("cars", cars);
        request.setAttribute("selectedType", type);
        request.getRequestDispatcher("/WEB-INF/views/cars.jsp").forward(request, response);
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws DataException, ServletException, IOException {
        Car car = carDAO.getById(FormUtil.get(request, "id"));
        if (car == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
            return;
        }
        request.setAttribute("car", car);
        request.getRequestDispatcher("/WEB-INF/views/car-details.jsp").forward(request, response);
    }
}
