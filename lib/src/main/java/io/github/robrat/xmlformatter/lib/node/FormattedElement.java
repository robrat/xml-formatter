package io.github.robrat.xmlformatter.lib.node;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

public class FormattedElement extends FormattedTreeNode {

  @Getter private final String postfix;

  public FormattedElement(@NonNull String formatted) {
    this(formatted, Collections.emptyList(), null);
  }

  public FormattedElement(
      @NonNull String formatted, @NonNull List<FormattedNode> children, String postfix) {
    super(formatted, children);
    this.postfix = postfix;
  }

  @Override
  public void accept(FormattedNodeVisitor visitor) {
    visitor.visitElement(this);
  }
}
