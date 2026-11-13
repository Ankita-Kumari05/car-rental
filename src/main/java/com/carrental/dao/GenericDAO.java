package com.carrental.dao;

import com.carrental.util.DataException;

import java.util.List;

/**
 * The basic operations every DAO must provide (an interface = a contract).
 * T is the type of object the DAO works with (Car, Booking or User).
 */
public interface GenericDAO<T> {

    void add(T item) throws DataException;        // Create

    List<T> getAll() throws DataException;        // Read all

    T getById(String id) throws DataException;    // Read one (null if not found)

    void update(T item) throws DataException;     // Update

    void delete(String id) throws DataException;  // Delete
}
