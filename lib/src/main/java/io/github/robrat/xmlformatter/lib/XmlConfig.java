package io.github.robrat.xmlformatter.lib;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class XmlConfig {

  private final String indent;

  public static XmlConfig defaults() {
    return XmlConfig.builder().indent("    ").build();
  }
}
