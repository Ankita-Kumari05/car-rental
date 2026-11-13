package com.carrental.util;

import jakarta.servlet.http.HttpServletRequest;

/** Small helpers for reading form fields safely (no nulls, no extra spaces). */
public class FormUtil {

    private FormUtil() {
    }

    /** Request parameter as a trimmed string. Returns "" if the parameter is missing. */
    public static String get(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    public static int getInt(HttpServletRequest request, String name, int defaultValue) {
        try {
            return Integer.parseInt(get(request, name));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double getDouble(HttpServletRequest request, String name, double defaultValue) {
        try {
            return Double.parseDouble(get(request, name));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
