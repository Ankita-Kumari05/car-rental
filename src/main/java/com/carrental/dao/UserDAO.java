package com.carrental.dao;

import com.carrental.model.Admin;
import com.carrental.model.Customer;
import com.carrental.model.User;
import com.carrental.util.DataException;
import com.carrental.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/** Reads and writes users.xml. Creates an Admin or a Customer object depending on the role attribute. */
public class UserDAO implements GenericDAO<User> {
    private static final String FILE = "users.xml";
    private static final String TAG = "user";

    private User fromElement(Element e) {
        // polymorphism: the variable type is User, the object is Admin or Customer
        User user = "ADMIN".equals(e.getAttribute("role")) ? new Admin() : new Customer();
        user.setId(e.getAttribute("id"));
        user.setName(XMLUtil.getText(e, "name"));
        user.setEmail(XMLUtil.getText(e, "email"));
        user.setPassword(XMLUtil.getText(e, "password"));
        user.setPhone(XMLUtil.getText(e, "phone"));
        return user;
    }

    private void fillElement(Element e, User user) {
        e.setAttribute("role", user.getRole());
        XMLUtil.setText(e, "name", user.getName());
        XMLUtil.setText(e, "email", user.getEmail());
        XMLUtil.setText(e, "password", user.getPassword());
        XMLUtil.setText(e, "phone", user.getPhone());
    }

    @Override
    public List<User> getAll() throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            NodeList nodes = doc.getElementsByTagName(TAG);
            List<User> users = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                users.add(fromElement((Element) nodes.item(i)));
            }
            return users;
        }
    }

    @Override
    public User getById(String id) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Element e = XMLUtil.findById(XMLUtil.loadDocument(FILE), TAG, id);
            return e == null ? null : fromElement(e);
        }
    }

    /** Finds a user by e-mail (not case sensitive). Returns null if there is no such user. */
    public User getByEmail(String email) throws DataException {
        for (User user : getAll()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    /** Returns the user if e-mail and password match, otherwise null. */
    public User login(String email, String password) throws DataException {
        User user = getByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public void add(User user) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);

            List<String> ids = new ArrayList<>();
            for (User existing : getAll()) {
                ids.add(existing.getId());
            }
            user.setId(XMLUtil.nextId("U", 1, 3, ids));

            Element e = doc.createElement(TAG);
            e.setAttribute("id", user.getId());
            fillElement(e, user);
            doc.getDocumentElement().appendChild(e);
            XMLUtil.saveDocument(doc, FILE);
        }
    }

    @Override
    public void update(User user) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            Element e = XMLUtil.findById(doc, TAG, user.getId());
            if (e == null) {
                throw new DataException("User not found: " + user.getId());
            }
            fillElement(e, user);
            XMLUtil.saveDocument(doc, FILE);
        }
    }

    @Override
    public void delete(String id) throws DataException {
        synchronized (XMLUtil.LOCK) {
            Document doc = XMLUtil.loadDocument(FILE);
            Element e = XMLUtil.findById(doc, TAG, id);
            if (e == null) {
                throw new DataException("User not found: " + id);
            }
            e.getParentNode().removeChild(e);
            XMLUtil.saveDocument(doc, FILE);
        }
    }
}
