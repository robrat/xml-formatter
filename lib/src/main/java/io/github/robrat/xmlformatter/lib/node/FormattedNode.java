package io.github.robrat.xmlformatter.lib.node;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class FormattedNode {

  @Getter @NonNull private final String formatted;

  public abstract void accept(FormattedNodeVisitor visitor);
}
