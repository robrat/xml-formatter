package io.github.robrat.xmlformatter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class XmlFileTreeVisitor implements FileVisitor<Path> {

  private final List<Pattern> excludeFiles;
  private final List<Pattern> excludePaths;

  @Getter final List<Path> xmlFiles = new ArrayList<>();

  public XmlFileTreeVisitor(List<String> excludeFiles, List<String> excludePaths) {
    this.excludeFiles = asPatterns(excludeFiles);
    this.excludePaths = asPatterns(excludePaths);
  }

  private static List<Pattern> asPatterns(List<String> list) {
    if (list == null || list.size() == 0) {
      return Collections.emptyList();
    }
    ArrayList<Pattern> result = new ArrayList<>(list.size());
    for (String name : list) {
      Pattern pattern = Pattern.compile(name);
      result.add(pattern);
    }
    return result;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    if (isDirExcluded(dir)) {
      return FileVisitResult.SKIP_SUBTREE;
    }
    return FileVisitResult.CONTINUE;
  }

  private boolean isDirExcluded(Path dir) {
    if (excludePaths.isEmpty()) {
      return false;
    }
    Path lastDir = dir.getName(dir.getNameCount() - 1);
    String lastDirName = lastDir.toString();
    for (Pattern excludePattern : excludePaths) {
      if (excludePattern.matcher(lastDirName).matches()) {
        log.trace("Excluded path '{}' because match of pattern '{}'", lastDirName, excludePattern);
        return true;
      }
    }
    return false;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    if (file.toString().endsWith(".xml") && !isFileExcluded(file)) {
      xmlFiles.add(file);
    }
    return FileVisitResult.CONTINUE;
  }

  private boolean isFileExcluded(Path file) {
    String filename = file.getName(file.getNameCount() - 1).toString();
    for (Pattern excludePattern : excludeFiles) {
      if (excludePattern.matcher(filename).matches()) {
        log.trace("Excluded file '{}' because match of pattern '{}'", filename, excludePattern);
        return true;
      }
    }
    return false;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) {
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
    return FileVisitResult.CONTINUE;
  }
}
