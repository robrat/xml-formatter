package io.github.robrat.xmlformatter.lib.util;

public final class StringUtils {

  private StringUtils() {}

  public static String trimLeft(String s) {
    int i = 0;
    while (i < s.length() && s.charAt(i) <= ' ') {
      i++;
    }
    return s.substring(i);
  }

  public static String trimRight(String s) {
    int i = s.length() - 1;
    while (i >= 0 && s.charAt(i) <= ' ') {
      i--;
    }
    return s.substring(0, i + 1);
  }

  public static String strip(String s) {
    int st = 0;
    int len = s.length();

    while (st < len && s.charAt(st) == ' ') {
      st++;
    }
    while (st < len && s.charAt(len - 1) == ' ') {
      len--;
    }
    return s.substring(st, len);
  }

  public static String stripLeft(String s) {
    int i = 0;
    while (i < s.length() && s.charAt(i) == ' ') {
      i++;
    }
    return s.substring(i);
  }

  public static String stripRight(String s) {
    int i = s.length() - 1;
    while (i >= 0 && s.charAt(i) == ' ') {
      i--;
    }
    return s.substring(0, i + 1);
  }

  public static int countNewlines(String s) {
    int i = 0, count = 0, len = s.length();
    while (i < len) {
      if (s.charAt(i) == '\n') {
        count++;
      }
      i++;
    }
    return count;
  }
}
