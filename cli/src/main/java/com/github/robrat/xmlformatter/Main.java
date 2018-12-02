package com.github.robrat.xmlformatter;

import com.github.robrat.xmlformatter.lib.XmlConfig;
import com.github.robrat.xmlformatter.lib.XmlFormatter;
import com.github.robrat.xmlformatter.lib.exception.EmptyFileException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) {
    XmlConfig xmlConfig = XmlConfig.defaults();
    for (String filename : args) {
      try {
        XmlFormatter xmlFormatter = XmlFormatter.ofXmlFile(filename);

        XmlFormatter.FormattedXml formatted = xmlFormatter.format(xmlConfig);

        if (formatted.isModified()) {
          System.out.println("Formatted " + filename);
          try (PrintWriter out = new PrintWriter(Paths.get(filename).toFile())) {
            out.println(formatted.getXml());
            break;
          }
        }
      } catch (EmptyFileException e) {
        System.err.println("Could not process " + filename + ": File is empty.");
      } catch (Exception e) {
        System.err.println("Error while processing " + filename);
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
