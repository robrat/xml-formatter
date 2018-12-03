package io.github.robrat.xmlformatter.maven;

import io.github.robrat.xmlformatter.lib.XmlConfig;
import io.github.robrat.xmlformatter.lib.XmlFormatter;
import io.github.robrat.xmlformatter.lib.exception.EmptyFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.SAXException;

@Mojo(name = "format", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true)
public class XmlFormatterMojo extends AbstractMojo {

  @Parameter(
      defaultValue = "${project.build.sourceDirectory}",
      property = "sourceDirectory",
      required = true)
  private File sourceDirectory;

  @Parameter(property = "scanSourceDirectory", defaultValue = "false")
  private boolean scanSourceDirectory;

  @Parameter(
      defaultValue = "${project.build.testSourceDirectory}",
      property = "testSourceDirectory",
      required = true)
  private File testSourceDirectory;

  @Parameter(property = "scanTestSourceDirectory", defaultValue = "false")
  private boolean scanTestSourceDirectory;

  @Parameter(property = "additionalSourceDirectories")
  private File[] additionalSourceDirectories;

  @Parameter(property = "filePattern", defaultValue = ".*\\.content.xml")
  private String filePattern;

  @Parameter(property = "verbose", defaultValue = "false")
  private boolean verbose;

  @Parameter(property = "check", defaultValue = "false")
  private boolean check;

  @Parameter(property = "displayLimit", defaultValue = "100")
  private int displayLimit;

  private List<Path> notFormatted = new ArrayList<>();

  @Parameter(property = "fmt.xml.skip", defaultValue = "false")
  private boolean skip;

  public void execute() throws MojoExecutionException {
    if (skip) {
      getLog().info("Skipping xml formatting");
      return;
    }

    List<Path> files = collectFiles();
    if (files.isEmpty()) {
      return;
    }

    XmlConfig xmlConfig = XmlConfig.defaults();
    int formatted = formatFiles(files, xmlConfig);

    if (check && notFormatted.size() > 0) {
      for (int i = 0, len = notFormatted.size();
          i < len && (displayLimit < 0 || i < displayLimit);
          i++) {
        getLog().error("File " + notFormatted.get(i) + " is not formatted");
      }
      throw new MojoExecutionException("Not all files are properly formatted");
    }

    getLog().info("Formatted " + formatted + " of " + files.size() + " files");
  }

  private List<Path> collectFiles() throws MojoExecutionException {
    try {
      List<File> dirs = mergeDirectories();
      if (dirs.isEmpty()) {
        getLog().info("No directories configured to scan for xml files.");
        return Collections.emptyList();
      }

      FileCollector fileCollector = new FileCollector(filePattern);
      for (File dir : dirs) {
        getLog().debug("Start scanning for file in dir: " + dir);
        Files.walkFileTree(dir.toPath(), fileCollector);
      }
      return fileCollector.getFiles();
    } catch (IOException e) {
      throw new MojoExecutionException("Could not collect files: " + e.getMessage(), e);
    }
  }

  private List<File> mergeDirectories() {
    if (!scanSourceDirectory
        && !scanTestSourceDirectory
        && additionalSourceDirectories.length == 0) {
      return Collections.emptyList();
    }
    List<File> dirs = new ArrayList<>();
    if (scanSourceDirectory && sourceDirectory.exists()) {
      dirs.add(sourceDirectory);
    }
    if (scanTestSourceDirectory && testSourceDirectory.exists()) {
      dirs.add(testSourceDirectory);
    }
    for (File dir : additionalSourceDirectories) {
      if (dir.exists()) {
        dirs.add(dir);
      }
    }
    return dirs;
  }

  private int formatFiles(List<Path> files, XmlConfig xmlConfig) throws MojoExecutionException {
    int count = 0;
    for (Path file : files) {
      boolean formatted = formatFile(file, xmlConfig);
      if (formatted) {
        count++;
      }
    }
    return count;
  }

  private boolean formatFile(Path file, XmlConfig xmlConfig) throws MojoExecutionException {
    try {
      XmlFormatter formatter = XmlFormatter.ofXmlFile(file);
      XmlFormatter.FormattedXml xmlFormatted = formatter.format(xmlConfig);

      if (xmlFormatted.isModified()) {
        if (check) {
          notFormatted.add(file);
          return false;
        }
        if (verbose) {
          getLog().info("Writing formatted xml file " + file);
        }
        Files.write(file, xmlFormatted.getXml().getBytes());
        return true;
      }
      return false;
    } catch (EmptyFileException e) {
      getLog().warn("Could not format file: " + file + ": File is empty.");
      return false;
    } catch (IOException | SAXException | ParserConfigurationException e) {
      throw new MojoExecutionException("Could not format file: " + file, e);
    }
  }
}
