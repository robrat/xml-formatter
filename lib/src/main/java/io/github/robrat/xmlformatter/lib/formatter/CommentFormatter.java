package io.github.robrat.xmlformatter.lib.formatter;

import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.node.FormattedComment;
import org.w3c.dom.Comment;

public class CommentFormatter implements NodeFormatter<Comment, FormattedComment> {

  public FormattedComment format(Comment node, String currentIndent, XmlConfig config) {
    String trimmed = node.getTextContent().trim();

    if (trimmed.indexOf('\n') > 0) {
      return multiLine(trimmed, currentIndent, config);
    }
    return singleLine(trimmed, currentIndent);
  }

  private FormattedComment singleLine(String trimmed, String currentIndent) {
    return new FormattedComment(currentIndent + "<!-- " + trimmed + " -->");
  }

  private FormattedComment multiLine(String trimmed, String currentIndent, XmlConfig config) {
    String[] splitted = trimmed.split("\n");

    StringBuilder sb = new StringBuilder(trimmed.length() + 50);
    sb.append(currentIndent).append("<!--");

    for (String line : splitted) {
      line = line.trim();
      sb.append('\n').append(currentIndent).append(config.getIndent()).append(line);
    }
    sb.append('\n').append(currentIndent).append("-->");

    return new FormattedComment(sb.toString());
  }
}
