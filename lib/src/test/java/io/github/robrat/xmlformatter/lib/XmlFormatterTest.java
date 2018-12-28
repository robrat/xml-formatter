package io.github.robrat.xmlformatter.lib;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class XmlFormatterTest {

  @Test
  public void nestedElementWithText() throws Exception {
    String xml = "<foo><bar>dummy content</bar></foo>";

    String xmlExpected = "<foo>\n" + "    <bar>dummy content</bar>\n" + "</foo>\n";

    XmlConfig xmlConfig = XmlConfig.builder().indent("    ").build();
    XmlFormatter xmlFormatter = XmlFormatter.ofXmlString(xml);

    XmlFormatter.FormattedXml xmlFormatted = xmlFormatter.format(xmlConfig);

    assertThat(xmlFormatted).isNotNull();
    assertThat(xmlFormatted.isModified()).isTrue();
    assertThat(xmlFormatted.getXml()).isEqualTo(xmlExpected);
  }
}
