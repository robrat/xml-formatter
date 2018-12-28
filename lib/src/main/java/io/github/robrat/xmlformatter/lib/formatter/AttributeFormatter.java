package io.github.robrat.xmlformatter.lib.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class AttributeFormatter {

  static void printAttributes(StringBuilder sb, Node node, int attributeOffset) {
    List<Node> attributeNodes = sortAttributeNodes(node);
    if (attributeNodes.isEmpty()) {
      return;
    }

    String attributeIndent = createAttributeIndent(attributeOffset);

    boolean first = true;
    for (Node attributeNode : attributeNodes) {
      if (first) {
        sb.append(' ');
        first = false;
      } else {
        sb.append('\n');
        sb.append(attributeIndent);
      }
      sb.append(attributeNode.getNodeName());
      sb.append("=\"");
      sb.append(attributeNode.getNodeValue().replaceAll("\"", "&quot;"));
      sb.append('"');
    }
  }

  private static List<Node> sortAttributeNodes(Node node) {
    return sortAttributeNodes(node.getAttributes());
  }

  static List<Node> sortAttributeNodes(NamedNodeMap attributeMap) {
    if (attributeMap == null) {
      return Collections.emptyList();
    }

    ArrayList<Node> attributeNodes = new ArrayList<>();
    for (int i = 0, len = attributeMap.getLength(); i < len; i++) {
      attributeNodes.add(attributeMap.item(i));
    }
    attributeNodes.sort(AttributeFormatter::compare);

    return attributeNodes;
  }

  private static String createAttributeIndent(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }

  private static int compare(Node n1, Node n2) {
    return compareNodeNames(n1.getNodeName(), n2.getNodeName());
  }

  static int compareNodeNames(String nodeName1, String nodeName2) {
    String ns1 = extractNamespacePrefix(nodeName1);
    String ns2 = extractNamespacePrefix(nodeName2);

    if (ns1 == null && ns2 == null) {
      return nodeName1.compareTo(nodeName2);
    }
    if (ns1 != null && ns2 == null) {
      return -1;
    }
    if (ns1 == null /* && ns2 != null */) {
      return 1;
    }

    // ns1 != null && ns2 != null

    boolean nsDecl1 = ns1.startsWith("xmlns");
    boolean nsDecl2 = ns2.startsWith("xmlns");

    if (nsDecl1 && nsDecl2) {
      return nodeName1.compareTo(nodeName2);
    }

    if (nsDecl1 /* && !nsDecl2 */) {
      return -1;
    }
    if (
    /* !nsDecl1 && */ nsDecl2) {
      return 1;
    }

    return nodeName1.compareTo(nodeName2);
  }

  private static String extractNamespacePrefix(String name) {
    if ("xmlns".equals(name)) {
      return "xmlns";
    }

    int idx = name.indexOf(':');
    if (idx < 0) {
      return null;
    }
    return name.substring(0, idx);
  }
}
