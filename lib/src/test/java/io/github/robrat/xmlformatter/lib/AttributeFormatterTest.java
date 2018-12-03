package io.github.robrat.xmlformatter.lib;

import static io.github.robrat.xmlformatter.lib.AttributeFormatter.compareNodeNames;
import static org.assertj.core.api.Java6Assertions.assertThat;

import org.junit.Test;

public class AttributeFormatterTest {

  @Test
  public void testCompareNodeNames() {
    assertThat(compareNodeNames("bar", "foo")).isLessThan(0);
    assertThat(compareNodeNames("foo", "bar")).isGreaterThan(0);
    assertThat(compareNodeNames("bar", "bar")).isEqualTo(0);

    assertThat(compareNodeNames("xmlns:sling", "bar")).isLessThan(0);
    assertThat(compareNodeNames("bar", "xmlns:sling")).isGreaterThan(0);
    assertThat(compareNodeNames("xmlns:sling", "xmlns:sling")).isEqualTo(0);

    assertThat(compareNodeNames("sling:resourceType", "bar")).isLessThan(0);
    assertThat(compareNodeNames("bar", "sling:resourceType")).isGreaterThan(0);
    assertThat(compareNodeNames("sling:resourceType", "sling:resourceType")).isEqualTo(0);

    assertThat(compareNodeNames("xmlns:sling", "sling:resourceType")).isLessThan(0);
    assertThat(compareNodeNames("sling:resourceType", "xmlns:sling")).isGreaterThan(0);
  }
}
