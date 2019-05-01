package io.github.robrat.xmlformatter;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class XmlFileTreeVisitor implements FileVisitor<Path> {

  @Getter final List<Path> xmlFiles = new ArrayList<>();

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    if (dir.endsWith("target")) {
      return FileVisitResult.SKIP_SUBTREE;
    }
    Path lastDir = dir.getName(dir.getNameCount() - 1);
    if (lastDir.toString().startsWith(".")) {
      return FileVisitResult.SKIP_SUBTREE;
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    if (file.toString().endsWith(".xml")) {
      xmlFiles.add(file);
    }
    return FileVisitResult.CONTINUE;
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
