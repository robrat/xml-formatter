package io.github.robrat.xmlformatter.lib.visitor;

import io.github.robrat.xmlformatter.lib.node.FormattedComment;
import io.github.robrat.xmlformatter.lib.node.FormattedElement;
import io.github.robrat.xmlformatter.lib.node.FormattedNode;
import io.github.robrat.xmlformatter.lib.node.FormattedNodeVisitor;
import io.github.robrat.xmlformatter.lib.node.FormattedText;
import io.github.robrat.xmlformatter.lib.node.FormattedTreeNode;
import io.github.robrat.xmlformatter.lib.node.RootNode;
import lombok.Getter;
import lombok.NonNull;

public class StringVisitor implements FormattedNodeVisitor {

  @Getter private final StringBuilder sb;

  public StringVisitor() {
    this(new StringBuilder(100));
  }

  public StringVisitor(int capacity) {
    this(new StringBuilder(capacity));
  }

  public StringVisitor(@NonNull StringBuilder sb) {
    this.sb = sb;
  }

  private void visitChildren(FormattedTreeNode node) {
    for (FormattedNode child : node.getChildren()) {
      child.accept(this);
    }
  }

  @Override
  public void visitRoot(RootNode node) {
    sb.append(node.getFormatted());
    visitChildren(node);
    sb.append('\n');
  }

  @Override
  public void visitElement(FormattedElement node) {
    if (sb.length() > 0) {
      sb.append('\n');
    }

    sb.append(node.getFormatted());

    if (node.getPostfix() == null) {
      return;
    }

    visitChildren(node);
    sb.append(node.getPostfix());
  }

  @Override
  public void visitComment(FormattedComment node) {
    if (sb.length() > 0) {
      sb.append('\n');
    }
    sb.append(node.getFormatted());
  }

  @Override
  public void visitText(FormattedText node) {
    sb.append(node.getFormatted());
  }
}
