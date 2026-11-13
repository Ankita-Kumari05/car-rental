package com.carrental.util;

/** Our own checked exception. Thrown when an XML file cannot be read or written. */
public class DataException extends Exception {

    public DataException(String message) {
        super(message);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
