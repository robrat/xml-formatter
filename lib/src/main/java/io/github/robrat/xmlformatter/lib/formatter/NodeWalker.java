package io.github.robrat.xmlformatter.lib.formatter;

import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.node.RootNode;
import org.w3c.dom.Document;

public interface NodeWalker {

  RootNode walk(Document document, XmlConfig config);
}
