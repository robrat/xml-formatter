package io.github.robrat.xmlformatter;

import com.beust.jcommander.JCommander;
import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.XmlFormatter;
import io.github.robrat.xmlformatter.lib.exception.EmptyFileException;
import io.github.robrat.xmlformatter.lib.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.spi.StandardLevel;

@Log4j2
public class Main {

  public static void main(String[] args) throws Exception {
    CliArguments cliArgs = new CliArguments();
    JCommander jct = JCommander.newBuilder().addObject(cliArgs).build();
    jct.parse(args);

    if (cliArgs.isHelp()) {
      jct.setProgramName("xml-formatter-cli");
      jct.usage();
      System.exit(0);
    }

    log.trace("Cli args: {}", cliArgs);

    applyLogLevel(cliArgs.getLoglevel());

    List<Path> xmlFiles =
        collectXmlFiles(cliArgs.getFiles(), cliArgs.getExcludeFiles(), cliArgs.getExcludePaths());
    XmlConfig xmlConfig = createXmlConfig(cliArgs);

    processFiles(xmlFiles, xmlConfig, cliArgs.isValidate());
  }

  private static void processFiles(List<Path> xmlFiles, XmlConfig xmlConfig, boolean validateOnly) {
    int nFormatted = 0;
    for (Path xmlFile : xmlFiles) {
      log.trace("Processing {} ...", xmlFile);
      try {
        XmlFormatter xmlFormatter = XmlFormatter.ofXmlFile(xmlFile);

        XmlFormatter.FormattedXml formatted = xmlFormatter.format(xmlConfig);

        if (formatted.isModified()) {
          nFormatted++;
          if (validateOnly) {
            log.error("Not properly formatted: " + xmlFile);
          } else {
            log.debug("Formatted {}", xmlFile);
            try (PrintWriter out = new PrintWriter(xmlFile.toFile())) {
              out.println(formatted.getXml());
            }
          }
        } else {
          log.trace("File {} is already formatted", xmlFile);
        }
      } catch (EmptyFileException e) {
        log.warn("Could not process {}: File is empty.", xmlFile);
      } catch (Exception e) {
        log.error("Error while processing " + xmlFile, e);
        System.exit(1);
      }
    }
    if (validateOnly) {
      if (nFormatted == 0) {
        log.info("All xml files are formatted");
        return;
      }
      if (nFormatted > 1) {
        log.error(nFormatted + " xml files are not formatted!");
      } else {
        log.error(nFormatted + " xml file is not formatted!");
      }
      System.exit(1);
    }

    log.info("Formatted {} xml files", nFormatted);
  }

  private static void applyLogLevel(StandardLevel logLevelParam) {
    if (logLevelParam == null) {
      return;
    }
    Level level = Level.getLevel(logLevelParam.name());
    if (level == null) {
      log.warn("Could not set level to {}", logLevelParam);
    } else {
      LoggerContext ctx = LoggerContext.getContext(false);
      Configuration conf = ctx.getConfiguration();
      conf.getRootLogger().setLevel(level);
      ctx.updateLoggers();
      log.debug("Changed default log level to {}", logLevelParam);
    }
  }

  private static XmlConfig createXmlConfig(CliArguments cliArgs) {
    if (cliArgs.getIndent() == null) {
      return XmlConfig.defaults();
    }
    String indent = StringUtils.repeat(' ', cliArgs.getIndent());
    return XmlConfig.builder().indent(indent).build();
  }

  private static List<Path> collectXmlFiles(
      List<File> names, List<String> excludeFiles, List<String> excludePaths) throws IOException {

    XmlFileTreeVisitor xmlFileTreeVisitor = new XmlFileTreeVisitor(excludeFiles, excludePaths);
    if (names == null || names.isEmpty()) {
      Path rootPath = Paths.get("").toAbsolutePath();
      log.debug("Collecting xml files in {} ...", rootPath);

      Files.walkFileTree(rootPath, xmlFileTreeVisitor);
      xmlFileTreeVisitor.getXmlFiles().forEach(System.out::println);
      return xmlFileTreeVisitor.getXmlFiles();
    }

    return Collections.emptyList();
  }
}
