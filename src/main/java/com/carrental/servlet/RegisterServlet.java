package com.carrental.servlet;

import com.carrental.dao.UserDAO;
import com.carrental.model.Customer;
import com.carrental.util.DataException;
import com.carrental.util.FormUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Handles /register: shows the form and creates a new Customer account. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = FormUtil.get(request, "name");
        String email = FormUtil.get(request, "email");
        String phone = FormUtil.get(request, "phone");
        String password = FormUtil.get(request, "password");
        String confirm = FormUtil.get(request, "confirm");

        // keep what the person typed so the form can be filled in again after an error
        request.setAttribute("formName", name);
        request.setAttribute("formEmail", email);
        request.setAttribute("formPhone", phone);

        String error = null;
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            error = "Please fill in all fields.";
        } else if (!email.contains("@") || !email.contains(".")) {
            error = "Enter a valid e-mail address.";
        } else if (password.length() < 6) {
            error = "Password must have at least 6 characters.";
        } else if (!password.equals(confirm)) {
            error = "The two passwords do not match.";
        }

        try {
            if (error == null && userDAO.getByEmail(email) != null) {
                error = "An account with this e-mail already exists.";
            }

            if (error != null) {
                request.setAttribute("error", error);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            Customer customer = new Customer(null, name, email, password, phone);
            userDAO.add(customer);
            response.sendRedirect(request.getContextPath() + "/login?msg=registered");
        } catch (DataException e) {
            throw new ServletException("Could not register: " + e.getMessage(), e);
        }
    }
}
