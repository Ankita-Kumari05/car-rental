package com.carrental.dao;

import com.carrental.model.Car;
import com.carrental.util.DataException;
import com.carrental.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/** Reads and writes cars.xml using the DOM parser. */
public class CarDAO implements GenericDAO<Car> {
    private static final String FILE = "cars.xml";
    private static final String TAG = "car";

    /** Converts one <car> XML element into a Car object. */
    private Car fromElement(Element e) {
        Car car = new Car();
        car.setId(e.getAttribute("id"));
        car.setName(XMLUtil.getText(e, "name"));
        car.setType(XMLUtil.getText(e, "type"));
        car.setFuel(XMLUtil.getText(e, "fuel"));
        car.setTransmission(XMLUtil.getText(e, "transmission"));
        car.setSeats(XMLUtil.getInt(e, "seats"));
        car.setPricePerDay(XMLUtil.getDouble(e, "pricePerDay"));
        car.setColor(XMLUtil.getText(e, "color"));
        car.setImage(XMLUtil.getText(e, "image"));
        car.setAvailable(XMLUtil.getBoolean(e, "available"));
        car.setDescription(XMLUtil.getText(e, "description"));
        return car;
    }

    /** Copies the values of a Car object into a <car> XML element. */
    private void fillElement(Element e, Car car) {
        XMLUtil.setText(e, "name", car.getName());
        XMLUtil.setText(e, "type", car.getType());
        XMLUtil.setText(e, "fuel", car.getFuel());
        XMLUtil.setText(e, "transmission", car.getTransmission());
        XMLUtil.setText(e, "seats", String.valueOf(car.getSeats()));
        XMLUtil.setText(e, "pricePerDay", String.valueOf((long) car.getPricePerDay()));
        XMLUtil.setText(e, "color", car.getColor());
        XMLUtil.setText(e, "image", car.getImage());
        XMLUtil.setText(e, "available", String.valueOf(car.isAvailable()));
        XMLUtil.setText(e, "description", car.getDescription());
    }

    @Override
    public List<Car> getAll() throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            NodeList nodes = doc.getElementsByTagName(TAG);
            List<Car> cars = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                cars.add(fromElement((Element) nodes.item(i)));
            }
            return cars;
        }
    }

    /** Only the cars the admin has marked as available. */
    public List<Car> getAvailable() throws DataException {
        List<Car> available = new ArrayList<>();
        for (Car car : getAll()) {
            if (car.isAvailable()) {
                available.add(car);
            }
        }
        return available;
    }

    @Override
    public Car getById(String id) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Element e = XMLUtil.findById(XMLUtil.loadDocument(FILE), TAG, id);
            return e == null ? null : fromElement(e);
        }
    }

    @Override
    public void add(Car car) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);

            List<String> ids = new ArrayList<>();
            for (Car existing : getAll()) {
                ids.add(existing.getId());
            }
            car.setId(XMLUtil.nextId("C", 1, 3, ids));

            Element e = doc.createElement(TAG);
            e.setAttribute("id", car.getId());
            fillElement(e, car);
            doc.getDocumentElement().appendChild(e);
            XMLUtil.saveDocument(doc, FILE);
        }
    }

    @Override
    public void update(Car car) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            Element e = XMLUtil.findById(doc, TAG, car.getId());
            if (e == null) {
                throw new DataException("Car not found: " + car.getId());
            }
            fillElement(e, car);
            XMLUtil.saveDocument(doc, FILE);
        }
    }

    @Override
    public void delete(String id) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            Element e = XMLUtil.findById(doc, TAG, id);
            if (e == null) {
                throw new DataException("Car not found: " + id);
            }
            e.getParentNode().removeChild(e);
            XMLUtil.saveDocument(doc, FILE);
        }
    }
}
