package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kc on 11/26/16.
 */
public class Helper {
  private static final String STOP_WORD = "data/english.stop";
  private static final boolean verbose = true;

  public static String porterStem(String token) {
    if (token == null | token.isEmpty()) {
      return null;
    }

    token = token.replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
    Stemmer stemmer = new Stemmer();
    stemmer.add(token.toCharArray(), token.length());
    stemmer.stem();

    return stemmer.toString().trim().toLowerCase();
  }

  public static String convertToUTF8(String s) {
    String result = null;
    try {
      result = new String(s.getBytes(Charset.defaultCharset()), "UTF-8");
    } catch(Exception e) {}
    return result;
  }

  public static Set<String> getStopWords() {
    Set<String> result = new HashSet<>();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(STOP_WORD));
      String line;
      while ((line = reader.readLine()) != null) {
        result.add(Helper.porterStem(line));
      }
      reader.close();
    } catch (IOException e) {
      System.out.println("Warning: invalid stop word file: " + STOP_WORD);
    }

    return result;
  }

  public static void printVerbose(String s) {
    if (verbose) {
      System.out.println(s);
    }
  }

  public static int convertToTime(String time) {
    String[] times = time.split(":");
    int min = Integer.valueOf(times[0]);
    int sec = Integer.valueOf(times[1]);

    return min * 100 + sec;
  }

  public static int postPeriod(String time) {
    if (time == null || time.trim().isEmpty()) {
      return 1;
    }

    String[] times = time.split(" ");
    int year = Integer.valueOf(times[1]);
    int month = getMonth(times[0]);

    LocalDateTime now = LocalDateTime.now();
    int currYear = now.getYear();
    int currMonth = now.getMonthValue();

    return (currYear - year) * 12 + currMonth - month + 1;
  }

  private static int getMonth(String time) {
    switch(time.toLowerCase()) {
      case "jan":
        return 1;
      case "feb":
        return 2;
      case "mar":
        return 3;
      case "apr":
        return 4;
      case "may":
        return 5;
      case "jun":
        return 6;
      case "jul":
        return 7;
      case "aug":
        return 8;
      case "sep":
        return 9;
      case "oct":
        return 10;
      case "nov":
        return 11;
      case "dec":
        return 12;
      default: return 0;
    }
  }
}