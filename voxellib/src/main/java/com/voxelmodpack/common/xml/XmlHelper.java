package com.voxelmodpack.common.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML helper functions to make working with XML less of a nuisance
 * 
 * @author Adam Mummery-Smith
 */
public class XmlHelper {
    /**
     * xpath evaluator
     */
    private static XPath xpath;

    /**
     * Namespace context helper for resolving namespaces
     */
    private static XmlNamespaceContext staticNamespaceContext;

    /**
     * Factory for document builders
     */
    private static DocumentBuilderFactory documentBuilderFactory;

    /**
     * Document builder
     */
    private static DocumentBuilder documentBuilder;

    /**
     * Factory for transformers (not the big robotic kind, the small boring
     * kind)
     */
    private static TransformerFactory transformerFactory;

    /**
     * Robots in disguise! (Wait, no...)
     */
    private static Transformer transformer;

    static {
        // XML namespace manager which is used if a context is not specified
        XmlHelper.staticNamespaceContext = new XmlNamespaceContext();

        // Create the xpath evaluator
        XmlHelper.xpath = XPathFactory.newInstance().newXPath();
        XmlHelper.xpath.setNamespaceContext(XmlHelper.staticNamespaceContext);

        // Create the document builder factory and enable namespaces
        XmlHelper.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        XmlHelper.documentBuilderFactory.setNamespaceAware(true);
        XmlHelper.createDocumentBuilder(XmlHelper.documentBuilderFactory);

        // Create the transformer factory
        XmlHelper.transformerFactory = TransformerFactory.newInstance();
        XmlHelper.createTransformer(XmlHelper.transformerFactory);
    }

    /**
     * Attempts to create the document builder if one is not initialised
     * already. This may fail in which case the function will return FALSE
     * 
     * @param factory
     * @return
     */
    protected static boolean createDocumentBuilder(DocumentBuilderFactory factory) {
        if (XmlHelper.documentBuilder == null) {
            try {
                // Create the document builder
                XmlHelper.documentBuilder = XmlHelper.documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Attempts to create the dom transformer if one is not initialised already.
     * This may fail in which case the function will return FALSE
     * 
     * @param factory
     * @return
     */
    protected static boolean createTransformer(TransformerFactory factory) {
        if (XmlHelper.transformer == null) {
            try {
                XmlHelper.transformer = XmlHelper.transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                return false;
            }

            // Set transformer properties for the desired output layout that we
            // need for transformation
            XmlHelper.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            XmlHelper.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            XmlHelper.transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }

        return true;
    }

    /**
     * Create and return a new, empty document. Returns null if the document
     * builder cannot be initialised
     * 
     * @return new document
     */
    public static Document newDocument() {
        return (XmlHelper.createDocumentBuilder(XmlHelper.documentBuilderFactory))
                ? XmlHelper.documentBuilder.newDocument() : null;
    }

    /**
     * Loads a document from the specified URI. Returns null if the document
     * builder cannot be initialised
     * 
     * @param uri URI to load the xml from
     * @return new document
     */
    public static Document getDocument(String uri) throws SAXException, IOException {
        return (XmlHelper.createDocumentBuilder(XmlHelper.documentBuilderFactory))
                ? XmlHelper.documentBuilder.parse(uri) : null;
    }

    /**
     * Loads a document from the specified URI. Returns null if the document
     * builder cannot be initialised
     * 
     * @param is InputStream to load the xml from
     * @return new document
     */
    public static Document getDocument(InputStream is) throws SAXException, IOException {
        return (XmlHelper.createDocumentBuilder(XmlHelper.documentBuilderFactory)) ? XmlHelper.documentBuilder.parse(is)
                : null;
    }

    /**
     * Loads a document from the specified file. Returns null if the document
     * builder cannot be initialised
     * 
     * @param file File to load from
     * @return new document
     */
    public static Document getDocument(File file) throws SAXException, IOException {
        return (XmlHelper.createDocumentBuilder(XmlHelper.documentBuilderFactory))
                ? XmlHelper.documentBuilder.parse(file) : null;
    }

    /**
     * Attempts to save the specified document to the specified file
     * 
     * @param file File to save to
     * @param document DOM Document to save
     * @return True if the file was saved successfully, false if saving the file
     *         failed
     */
    public static boolean saveDocument(File file, Document document) {
        if (XmlHelper.createTransformer(XmlHelper.transformerFactory)) {
            try {
                XmlHelper.transformer.transform(new DOMSource(document), new StreamResult(file));
                return true;
            } catch (TransformerException e) {}
        }

        return false;
    }

    /**
     * @param document
     * @return
     */
    public static String getDocumentContent(Document document) {
        if (XmlHelper.createTransformer(XmlHelper.transformerFactory)) {
            try {
                StringWriter writer = new StringWriter();
                XmlHelper.transformer.transform(new DOMSource(document), new StreamResult(writer));
                writer.flush();
                return writer.getBuffer().toString();
            } catch (TransformerException e) {}
        }

        return null;
    }

    /**
     * Add a namespace prefix to the current static namespace manager
     * 
     * @param prefix
     * @param namespaceURI
     */
    public static void addNamespacePrefix(String prefix, String namespaceURI) {
        XmlHelper.staticNamespaceContext.addPrefix(prefix, namespaceURI);
    }

    /**
     * Clear namespace prefixes in the current static namespace context
     */
    public static void clearNamespacePrefixList() {
        XmlHelper.staticNamespaceContext.clear();
    }

    /**
     * Replace the current static namespace context with the supplied namespace
     * context
     * 
     * @param namespaceContext
     */
    public static void setNamespaceContext(XmlNamespaceContext namespaceContext) {
        if (namespaceContext != null) {
            XmlHelper.staticNamespaceContext = namespaceContext;
        }
    }

    /**
     * @param xml
     * @param xPath
     * @return
     */
    public static NodeList query(Node xml, String xPath) {
        return XmlHelper.query(xml, xPath, XmlHelper.staticNamespaceContext);
    }

    public static NodeList query(Node xml, String xPath, NamespaceContext namespaceContext) {
        try {
            XmlHelper.xpath.setNamespaceContext(namespaceContext);

            // System.out.println("Result is: " + xpath.evaluate(xPath, xml));
            return (NodeList) XmlHelper.xpath.evaluate(xPath, xml, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Node> queryAsArray(Node xml, String xPath) {
        return XmlHelper.queryAsArray(xml, xPath, XmlHelper.staticNamespaceContext);
    }

    public static ArrayList<Node> queryAsArray(Node xml, String xPath, NamespaceContext namespaceContext) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        NodeList result = XmlHelper.query(xml, xPath, namespaceContext);

        if (result != null) {
            for (int nodeIndex = 0; nodeIndex < result.getLength(); nodeIndex++)
                nodes.add(result.item(nodeIndex));
        }

        return nodes;
    }

    public static Node getNode(Node node, String nodeName) {
        return XmlHelper.getNode(node, nodeName, XmlHelper.staticNamespaceContext);
    }

    public static Node getNode(Node node, String nodeName, NamespaceContext namespaceContext) {
        try {
            XmlHelper.xpath.setNamespaceContext(namespaceContext);
            return (Node) XmlHelper.xpath.evaluate(nodeName, node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {}

        return null;
    }

    public static String getNodeValue(Node node, String nodeName, String defaultValue) {
        return XmlHelper.getNodeValue(node, nodeName, defaultValue, XmlHelper.staticNamespaceContext);
    }

    public static String getNodeValue(Node node, String nodeName, String defaultValue,
            NamespaceContext namespaceContext) {
        try {
            XmlHelper.xpath.setNamespaceContext(namespaceContext);
            NodeList nodes = (NodeList) XmlHelper.xpath.evaluate(nodeName, node, XPathConstants.NODESET);

            if (nodes.getLength() > 0)
                return nodes.item(0).getTextContent();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static int getNodeValue(Node node, String nodeName, int defaultValue) {
        return XmlHelper.getNodeValue(node, nodeName, defaultValue, XmlHelper.staticNamespaceContext);
    }

    public static int getNodeValue(Node node, String nodeName, int defaultValue, NamespaceContext namespaceContext) {
        String nodeValue = XmlHelper.getNodeValue(node, nodeName, "" + defaultValue, namespaceContext);

        try {
            return Integer.parseInt(nodeValue);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static String getAttributeValue(Node node, String attributeName, String defaultValue) {
        Node attribute = node.getAttributes().getNamedItem(attributeName);

        if (attribute != null) {
            return attribute.getTextContent();
        }

        return defaultValue;
    }

    public static int getAttributeValue(Node node, String attributeName, int defaultValue) {
        String attributeValue = XmlHelper.getAttributeValue(node, attributeName, "" + defaultValue);

        try {
            return Integer.parseInt(attributeValue);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
