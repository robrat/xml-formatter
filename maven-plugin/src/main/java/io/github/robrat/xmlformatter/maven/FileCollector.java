package io.github.robrat.xmlformatter.maven;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

final class FileCollector extends SimpleFileVisitor<Path> {

  private final List<Path> files = new ArrayList<>();
  private final String filePattern;

  FileCollector(String filePattern) {
    this.filePattern = filePattern;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    String filename = file.getFileName().toString();
    long size = Files.size(file);
    if (filename.matches(filePattern) && size > 0) {
      files.add(file);
    }
    return FileVisitResult.CONTINUE;
  }

  List<Path> getFiles() {
    return this.files;
  }
}
