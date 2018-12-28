package io.github.robrat.xmlformatter.lib.node;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;

public abstract class FormattedTreeNode extends FormattedNode {

  @Getter @NonNull private final List<FormattedNode> children;

  public FormattedTreeNode(@NonNull String formatted, @NonNull List<FormattedNode> children) {
    super(formatted);
    this.children = children;
  }
}
