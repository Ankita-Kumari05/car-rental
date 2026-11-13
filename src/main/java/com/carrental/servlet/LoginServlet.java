package com.carrental.servlet;

import com.carrental.dao.UserDAO;
import com.carrental.model.Admin;
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

/** Handles /login (show form + check credentials) and /logout. */
@WebServlet({"/login", "/logout"})
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("/logout".equals(request.getServletPath())) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();          // forget the logged-in user
            }
            response.sendRedirect(request.getContextPath() + "/login?msg=loggedout");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = FormUtil.get(request, "email");
        String password = FormUtil.get(request, "password");

        try {
            User user = userDAO.login(email, password);

            if (user == null) {
                request.setAttribute("error", "Incorrect e-mail or password.");
                request.setAttribute("formEmail", email);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // start a fresh session and store the logged-in user in it
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60);

            String ctx = request.getContextPath();
            if (user instanceof Admin) {
                response.sendRedirect(ctx + "/admin/dashboard");
            } else {
                response.sendRedirect(ctx + "/cars");
            }
        } catch (DataException e) {
            throw new ServletException("Could not log in: " + e.getMessage(), e);
        }
    }
}
