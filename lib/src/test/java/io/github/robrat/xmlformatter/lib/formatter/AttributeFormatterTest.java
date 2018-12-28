package io.github.robrat.xmlformatter.lib.formatter;

import static io.github.robrat.xmlformatter.lib.formatter.AttributeFormatter.compareNodeNames;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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

  @Test
  public void sortAttributeNodes_null() {
    List<Node> sorted = AttributeFormatter.sortAttributeNodes(null);
    assertThat(sorted).isEmpty();
  }

  @Test
  public void sortAttributeNodes_pomNamespace() {
    List<Attr> attributes =
        asList(
            mockAttr("xmlns", "http://maven.apache.org/POM/4.0.0"),
            mockAttr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"),
            mockAttr(
                "xsi:schemaLocation",
                "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"));

    NamedNodeMap namedNodeMap = mockNamedNodeMap(attributes);

    List<Node> sorted = AttributeFormatter.sortAttributeNodes(namedNodeMap);

    assertThat(sorted)
        .extracting(Node::getNodeName)
        .containsExactly("xmlns", "xmlns:xsi", "xsi:schemaLocation");
  }

  private static Attr mockAttr(String name, String value) {
    Attr attr = mock(Attr.class);
    when(attr.getNodeName()).thenReturn(name);
    when(attr.getNodeValue()).thenReturn(value);

    return attr;
  }

  private static NamedNodeMap mockNamedNodeMap(List<Attr> attributes) {
    NamedNodeMap namedNodeMap = mock(NamedNodeMap.class);
    when(namedNodeMap.getLength()).thenReturn(attributes.size());
    when(namedNodeMap.item(anyInt()))
        .thenAnswer(
            answer -> {
              Integer arg = answer.getArgument(0);
              return attributes.get(arg);
            });

    return namedNodeMap;
  }
}
