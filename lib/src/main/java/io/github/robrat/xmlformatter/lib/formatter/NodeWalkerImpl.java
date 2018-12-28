package io.github.robrat.xmlformatter.lib.formatter;

import static io.github.robrat.xmlformatter.lib.formatter.AttributeFormatter.printAttributes;

import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.node.FormattedElement;
import io.github.robrat.xmlformatter.lib.node.FormattedNode;
import io.github.robrat.xmlformatter.lib.node.FormattedText;
import io.github.robrat.xmlformatter.lib.node.RootNode;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

@RequiredArgsConstructor
public class NodeWalkerImpl implements NodeWalker {

  @NonNull private final TextFormatter textFormatter;

  @NonNull private final CommentFormatter commentFormatter;

  @Override
  public RootNode walk(Document document, XmlConfig config) {
    String declaration = createDeclaration(document);

    List<FormattedNode> children = new ArrayList<>();

    NodeList nodeList = document.getChildNodes();
    for (int i = 0, len = nodeList.getLength(); i < len; ++i) {
      Node child = nodeList.item(i);
      FormattedNode formattedNode = walk(child, "", config);
      children.add(formattedNode);
    }

    return new RootNode(declaration, children);
  }

  private String createDeclaration(Document document) {
    if (document.getXmlVersion() == null || document.getXmlEncoding() == null) {
      return "";
    }
    return "<?xml version=\""
        + document.getXmlVersion()
        + "\" encoding=\""
        + document.getXmlEncoding()
        + "\"?>";
  }

  private FormattedNode walk(Node node, String currentIndent, XmlConfig config) {
    if (node.getNodeType() == Node.COMMENT_NODE) {
      return commentFormatter.format((Comment) node, currentIndent, config);
    }
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      return createFormattedElement((Element) node, currentIndent, config);
    }
    if (node.getNodeType() == Node.TEXT_NODE) {
      return textFormatter.format((Text) node, currentIndent, config);
    }
    return null;
  }

  private FormattedElement createFormattedElement(
      Element node, String currentIndent, XmlConfig config) {
    StringBuilder sb =
        new StringBuilder(100).append(currentIndent).append('<').append(node.getNodeName());

    int lineLength = sb.length();

    printAttributes(sb, node, lineLength + 1);

    NodeList nodeList = node.getChildNodes();
    int nNodes = nodeList.getLength();

    if (nNodes == 0) {
      sb.append(" />");
      return new FormattedElement(sb.toString());
    }
    sb.append('>');

    String prefix = sb.toString();
    List<FormattedNode> children = new ArrayList<>();

    String childIndent = currentIndent + config.getIndent();

    FormattedNode formattedNode;
    for (int i = 0; i < nNodes; i++) {
      formattedNode = walk(nodeList.item(i), childIndent, config);
      if (formattedNode != null) {
        children.add(formattedNode);
      }
    }

    StringBuilder postfixSb = new StringBuilder();
    if (!containsOnlySingleLineText(children)) {
      postfixSb.append('\n').append(currentIndent);
    }
    postfixSb.append("</").append(node.getNodeName()).append('>');

    return new FormattedElement(prefix, children, postfixSb.toString());
  }

  private static boolean containsOnlySingleLineText(List<FormattedNode> children) {
    if (children.size() != 1) {
      return false;
    }
    FormattedNode child = children.get(0);
    if (child instanceof FormattedText) {
      FormattedText formattedText = (FormattedText) child;
      return formattedText.isSingleLine();
    }
    return false;
  }
}
