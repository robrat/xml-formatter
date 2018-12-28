package io.github.robrat.xmlformatter.lib.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StringUtilsTest {

  @Test
  public void strip() {
    assertThat(StringUtils.strip("foo")).isEqualTo("foo");
    assertThat(StringUtils.strip("  foo")).isEqualTo("foo");
    assertThat(StringUtils.strip("foo  ")).isEqualTo("foo");
    assertThat(StringUtils.strip("   ")).isEqualTo("");
  }
}
