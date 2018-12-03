package io.github.robrat.xmlformatter.lib.exception;

public class EmptyFileException extends XmlFormattingException {

  public EmptyFileException() {
    super("File does not contain any content.");
  }
}
