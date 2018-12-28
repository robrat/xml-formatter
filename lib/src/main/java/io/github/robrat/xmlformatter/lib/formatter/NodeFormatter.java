package io.github.robrat.xmlformatter.lib.formatter;

import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.node.FormattedNode;
import org.w3c.dom.Node;

public interface NodeFormatter<V extends Node, R extends FormattedNode> {

  R format(V node, String indent, XmlConfig config);
}
