package io.github.robrat.xmlformatter.lib.formatter;

import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.node.FormattedText;
import io.github.robrat.xmlformatter.lib.util.StringUtils;
import org.w3c.dom.Text;

public class TextFormatter implements NodeFormatter<Text, FormattedText> {

  public FormattedText format(Text node, String indent, XmlConfig xmlConfig) {
    String stripped = StringUtils.strip(node.getTextContent());
    int nNewlines = StringUtils.countNewlines(stripped);
    if (nNewlines > 1 && nNewlines == stripped.length()) {
      return new FormattedText("\n", false);
    }

    String trimmed = stripped.trim();
    if (trimmed.length() == 0) {
      return null;
    }

    if (trimmed.indexOf('\n') > 0) {
      String str = "\n" + indent + trimmed;
      return new FormattedText(str, false);
    }
    return new FormattedText(trimmed, true);
  }
}
