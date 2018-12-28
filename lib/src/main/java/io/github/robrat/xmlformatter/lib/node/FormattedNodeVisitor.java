package io.github.robrat.xmlformatter.lib.node;

public interface FormattedNodeVisitor {

  void visitRoot(RootNode node);

  void visitElement(FormattedElement node);

  void visitComment(FormattedComment node);

  void visitText(FormattedText node);
}
