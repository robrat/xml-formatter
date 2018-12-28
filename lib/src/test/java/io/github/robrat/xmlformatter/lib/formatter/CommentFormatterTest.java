package io.github.robrat.xmlformatter.lib.formatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.node.FormattedComment;
import org.junit.Test;
import org.w3c.dom.Comment;

public class CommentFormatterTest {

  @Test
  public void singleLineComment() {
    final String currentIndent = "  ";

    Comment comment = mock(Comment.class);
    when(comment.getTextContent()).thenReturn("foo");

    FormattedComment formatted = new CommentFormatter().format(comment, currentIndent, null);

    assertThat(formatted.getFormatted()).isEqualTo("  <!-- foo -->");
  }

  @Test
  public void multiLineComment() {
    final String currentIndent = "  ";

    XmlConfig xmlConfig = XmlConfig.builder().indent("  ").build();

    Comment comment = mock(Comment.class);
    when(comment.getTextContent()).thenReturn("foo\n    bar");

    FormattedComment formatted = new CommentFormatter().format(comment, currentIndent, xmlConfig);

    assertThat(formatted.getFormatted()).isEqualTo("  <!--\n    foo\n    bar\n  -->");
  }
}
