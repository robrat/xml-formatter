package io.github.robrat.xmlformatter.lib;

import io.github.robrat.xmlformatter.lib.exception.EmptyFileException;
import io.github.robrat.xmlformatter.lib.formatter.CommentFormatter;
import io.github.robrat.xmlformatter.lib.formatter.NodeWalkerImpl;
import io.github.robrat.xmlformatter.lib.formatter.TextFormatter;
import io.github.robrat.xmlformatter.lib.node.RootNode;
import io.github.robrat.xmlformatter.lib.visitor.StringVisitor;
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
import org.w3c.dom.Document;
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
    NodeWalkerImpl nodeWalker = new NodeWalkerImpl(new TextFormatter(), new CommentFormatter());

    RootNode rootNode = nodeWalker.walk(document, config);
    StringVisitor visitor = new StringVisitor(sb);

    rootNode.accept(visitor);
  }

  private static Document parseXml(String xml)
      throws IOException, SAXException, ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xml));
    return db.parse(is);
  }
}
