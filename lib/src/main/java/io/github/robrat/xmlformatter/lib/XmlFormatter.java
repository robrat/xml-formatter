package io.github.robrat.xmlformatter.lib;

import static io.github.robrat.xmlformatter.lib.AttributeFormatter.printAttributes;

import io.github.robrat.xmlformatter.lib.exception.EmptyFileException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class XmlFormatter {

  @Getter
  @RequiredArgsConstructor
  public static class FormattedXml {

    private final String xml;
    private final boolean modified;
  }

  private final String xml;
  private final Document document;

  public static XmlFormatter ofXmlFile(String xmlFilename)
      throws IOException, ParserConfigurationException, SAXException {
    Path file = Paths.get(xmlFilename);
    return ofXmlFile(file);
  }

  public static XmlFormatter ofXmlFile(Path file)
      throws IOException, ParserConfigurationException, SAXException {
    String xml = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    return ofXmlString(xml);
  }

  public static XmlFormatter ofXmlString(String xml)
      throws ParserConfigurationException, SAXException, IOException {
    String trimmed = xml.trim();
    if (trimmed.length() == 0) {
      throw new EmptyFileException();
    }
    Document doc = parseXml(xml);
    return new XmlFormatter(xml, doc);
  }

  public FormattedXml format(XmlConfig config) {
    StringBuilder sb = new StringBuilder(xml.length());

    formatTo(sb, config);

    String formattedXml = sb.toString();
    boolean modified = !xml.equals(formattedXml);

    return new FormattedXml(formattedXml, modified);
  }

  public void formatTo(StringBuilder sb, XmlConfig config) {
    printDeclaration(sb, document);

    NodeList children = document.getChildNodes();
    for (int i = 0, len = children.getLength(); i < len; ++i) {
      Node child = children.item(i);
      printNode(sb, child, "", config);
    }
  }

  private static Document parseXml(String xml)
      throws IOException, SAXException, ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xml));
    return db.parse(is);
  }

  private void printDeclaration(StringBuilder sb, Document document) {
    if (document.getXmlVersion() == null || document.getXmlEncoding() == null) {
      return;
    }
    sb.append("<?xml version=\"");
    sb.append(document.getXmlVersion());
    sb.append("\" encoding=\"");
    sb.append(document.getXmlEncoding());
    sb.append("\"?>");
    sb.append('\n');
  }

  private static void printNode(StringBuilder sb, Node node, String indent, XmlConfig config) {
    if (node.getNodeType() == Node.COMMENT_NODE) {
      printComment(sb, (Comment) node);
      return;
    }
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      printElementNode(sb, (Element) node, indent, config);
      return;
    }
    if (node.getNodeType() == Node.TEXT_NODE) {
      printText(sb, (Text) node, indent);
    }
  }

  private static void printElementNode(
      StringBuilder sb, Element node, String indent, XmlConfig config) {
    String line = indent + "<" + node.getNodeName();
    sb.append(line);

    printAttributes(sb, node, line.length() + 1);

    NodeList nodeList = node.getChildNodes();
    int nNodes = nodeList.getLength();

    if (nNodes == 0) {
      sb.append(" />\n");
      return;
    }
    sb.append(">\n");

    String childIndent = indent + config.getIndent();

    for (int i = 0; i < nNodes; i++) {
      printNode(sb, nodeList.item(i), childIndent, config);
    }
    sb.append(indent);
    sb.append("</");
    sb.append(node.getNodeName());
    sb.append(">\n");
  }

  private static void printText(StringBuilder sb, Text node, String indent) {
    String trimmed = node.getTextContent().trim();
    if (trimmed.length() > 0) {
      sb.append(indent);
      sb.append(trimmed);
      sb.append('\n');
    }
  }

  private static void printComment(StringBuilder sb, Comment node) {
    sb.append("<!--");
    sb.append(node.getTextContent());
    sb.append("-->\n");
  }
}
