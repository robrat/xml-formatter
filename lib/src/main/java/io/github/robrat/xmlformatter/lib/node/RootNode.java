package io.github.robrat.xmlformatter.lib.node;

import java.util.List;
import lombok.NonNull;

public class RootNode extends FormattedTreeNode {

  public RootNode(@NonNull String declaration, @NonNull List<FormattedNode> children) {
    super(declaration, children);
  }

  @Override
  public void accept(FormattedNodeVisitor visitor) {
    visitor.visitRoot(this);
  }
}
