package io.github.robrat.xmlformatter.lib.node;

import lombok.Getter;
import lombok.NonNull;

public class FormattedText extends FormattedNode {

  @Getter private final boolean singleLine;

  public FormattedText(@NonNull String formatted, boolean singleLine) {
    super(formatted);
    this.singleLine = singleLine;
  }

  @Override
  public void accept(FormattedNodeVisitor visitor) {
    visitor.visitText(this);
  }
}
