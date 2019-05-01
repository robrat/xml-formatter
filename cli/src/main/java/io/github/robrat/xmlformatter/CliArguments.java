package io.github.robrat.xmlformatter;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;
import java.util.List;
import lombok.Data;
import org.apache.logging.log4j.spi.StandardLevel;

@Data
class CliArguments {

  @Parameter(description = "The list of files to format", converter = FileConverter.class)
  private List<File> files;

  @Parameter(
      names = {"-i", "--indent"},
      description = "Number of spaces for indent")
  private Integer indent;

  @Parameter(
      names = {"--loglevel"},
      description = "Level of verbosity")
  private StandardLevel loglevel;

  @Parameter(
      names = {"--validate"},
      description = "Only print non formatted files")
  private boolean validate;

  @Parameter(
      names = {"-h", "--help"},
      help = true)
  private boolean help;
}
