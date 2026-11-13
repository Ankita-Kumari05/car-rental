package com.carrental.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Helper methods shared by all DAO classes: load / save XML files with the DOM parser
 * and read or write the text inside XML elements.
 *
 * Data files live in  <user home>/car-rental-data/  so they survive server restarts
 * and re-deployments. On the very first run the sample files are copied there
 * from src/main/resources/data.
 */
public class XMLUtil {

    /** All DAOs lock on this object so two requests never write a file at the same time. */
    public static final Object LOCK = new Object();

    private static final Path DATA_DIR = Paths.get(System.getProperty("user.home"), "car-rental-data");

    private XMLUtil() {
    }

    /** Returns the data file, copying the sample file first if it does not exist yet. */
    public static File getDataFile(String fileName) throws DataException {
        try {
            Files.createDirectories(DATA_DIR);
            Path target = DATA_DIR.resolve(fileName);
            if (!Files.exists(target)) {
                try (InputStream in = XMLUtil.class.getResourceAsStream("/data/" + fileName)) {
                    if (in == null) {
                        throw new DataException("Sample data file not found: " + fileName);
                    }
                    Files.copy(in, target);
                }
            }
            return target.toFile();
        } catch (IOException e) {
            throw new DataException("Could not prepare data file " + fileName, e);
        }
    }

    /** Reads an XML file into a DOM Document. */
    public static Document loadDocument(String fileName) throws DataException {
        try {
            File file = getDataFile(fileName);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);
            removeWhitespaceNodes(doc.getDocumentElement());
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new DataException("Could not read " + fileName, e);
        }
    }

    /** Writes a DOM Document back to its XML file (pretty printed). */
    public static void saveDocument(Document doc, String fileName) throws DataException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(doc), new StreamResult(getDataFile(fileName)));
        } catch (TransformerException e) {
            throw new DataException("Could not save " + fileName, e);
        }
    }

    /** Finds the element with the given tag name and id attribute, or null if there is none. */
    public static Element findById(Document doc, String tagName, String id) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.getAttribute("id").equals(id)) {
                return element;
            }
        }
        return null;
    }

    /** Text inside a child element, or "" when the child is missing. */
    public static String getText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0) {
            return "";
        }
        return list.item(0).getTextContent().trim();
    }

    public static int getInt(Element parent, String tagName) {
        try {
            return Integer.parseInt(getText(parent, tagName));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double getDouble(Element parent, String tagName) {
        try {
            return Double.parseDouble(getText(parent, tagName));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean getBoolean(Element parent, String tagName) {
        return Boolean.parseBoolean(getText(parent, tagName));
    }

    /** Sets the text of a child element. The child is created if it does not exist (used for both add and update). */
    public static void setText(Element parent, String tagName, String value) {
        NodeList list = parent.getElementsByTagName(tagName);
        Element child;
        if (list.getLength() == 0) {
            child = parent.getOwnerDocument().createElement(tagName);
            parent.appendChild(child);
        } else {
            child = (Element) list.item(0);
        }
        child.setTextContent(value == null ? "" : value);
    }

    /**
     * Creates the next id, for example nextId("C", 1, 3, ids) gives "C009" when the highest existing id is "C008".
     * prefix = letter at the start, start = first number to use, width = number of digits.
     */
    public static String nextId(String prefix, int start, int width, Collection<String> existingIds) {
        int max = start - 1;
        for (String id : existingIds) {
            try {
                int number = Integer.parseInt(id.substring(prefix.length()));
                if (number > max) {
                    max = number;
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // an id in a different format is simply ignored
            }
        }
        return prefix + String.format("%0" + width + "d", max + 1);
    }

    /** Removes blank text nodes so the file does not get extra empty lines every time it is saved. */
    private static void removeWhitespaceNodes(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes(child);
            }
        }
    }
}
