package io.github.robrat.xmlformatter.lib.node;

import lombok.NonNull;

public class FormattedComment extends FormattedNode {

  public FormattedComment(@NonNull String formatted) {
    super(formatted);
  }

  @Override
  public void accept(FormattedNodeVisitor visitor) {
    visitor.visitComment(this);
  }
}
