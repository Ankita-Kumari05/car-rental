package com.carrental.dao;

import com.carrental.model.Booking;
import com.carrental.util.DataException;
import com.carrental.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/** Reads and writes bookings.xml. */
public class BookingDAO implements GenericDAO<Booking> {
    private static final String FILE = "bookings.xml";
    private static final String TAG = "booking";

    private Booking fromElement(Element e) {
        Booking b = new Booking();
        b.setId(e.getAttribute("id"));
        b.setUserId(XMLUtil.getText(e, "userId"));
        b.setCustomerName(XMLUtil.getText(e, "customerName"));
        b.setCarId(XMLUtil.getText(e, "carId"));
        b.setCarName(XMLUtil.getText(e, "carName"));
        b.setDays(XMLUtil.getInt(e, "days"));
        b.setPricePerDay(XMLUtil.getDouble(e, "pricePerDay"));
        b.setTotalAmount(XMLUtil.getDouble(e, "totalAmount"));
        b.setStatus(XMLUtil.getText(e, "status"));
        b.setBookingDate(XMLUtil.getText(e, "bookingDate"));
        return b;
    }

    private void fillElement(Element e, Booking b) {
        XMLUtil.setText(e, "userId", b.getUserId());
        XMLUtil.setText(e, "customerName", b.getCustomerName());
        XMLUtil.setText(e, "carId", b.getCarId());
        XMLUtil.setText(e, "carName", b.getCarName());
        XMLUtil.setText(e, "days", String.valueOf(b.getDays()));
        XMLUtil.setText(e, "pricePerDay", String.valueOf((long) b.getPricePerDay()));
        XMLUtil.setText(e, "totalAmount", String.valueOf((long) b.getTotalAmount()));
        XMLUtil.setText(e, "status", b.getStatus());
        XMLUtil.setText(e, "bookingDate", b.getBookingDate());
    }

    @Override
    public List<Booking> getAll() throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            NodeList nodes = doc.getElementsByTagName(TAG);
            List<Booking> bookings = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                bookings.add(fromElement((Element) nodes.item(i)));
            }
            return bookings;
        }
    }

    /** All bookings made by one customer. */
    public List<Booking> getByUserId(String userId) throws DataException {
        List<Booking> result = new ArrayList<>();
        for (Booking b : getAll()) {
            if (b.getUserId().equals(userId)) {
                result.add(b);
            }
        }
        return result;
    }

    @Override
    public Booking getById(String id) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Element e = XMLUtil.findById(XMLUtil.loadDocument(FILE), TAG, id);
            return e == null ? null : fromElement(e);
        }
    }

    /** Saves a new booking and gives it a booking id such as B1006. */
    @Override
    public void add(Booking booking) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);

            List<String> ids = new ArrayList<>();
            for (Booking existing : getAll()) {
                ids.add(existing.getId());
            }
            booking.setId(XMLUtil.nextId("B", 1001, 4, ids));

            Element e = doc.createElement(TAG);
            e.setAttribute("id", booking.getId());
            fillElement(e, booking);
            doc.getDocumentElement().appendChild(e);
            XMLUtil.saveDocument(doc, FILE);
        }
    }

    @Override
    public void update(Booking booking) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            Element e = XMLUtil.findById(doc, TAG, booking.getId());
            if (e == null) {
                throw new DataException("Booking not found: " + booking.getId());
            }
            fillElement(e, booking);
            XMLUtil.saveDocument(doc, FILE);
        }
    }

    @Override
    public void delete(String id) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            Element e = XMLUtil.findById(doc, TAG, id);
            if (e == null) {
                throw new DataException("Booking not found: " + id);
            }
            e.getParentNode().removeChild(e);
            XMLUtil.saveDocument(doc, FILE);
        }
    }
}
