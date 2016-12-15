package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by kc on 12/15/16.
 */
public class QueryChinese extends Query {
  private static final String STOPWORDS_FILE = "conf/stopwords_zh.txt";
  private Vector<String> _stopwords = new Vector<>();
  private static ChineseSegmentor _segmentor;

  public QueryChinese(String query, ChineseSegmentor segmentor) {
    super(query);
    _segmentor = segmentor;
    setStopwords();
  }

  private void setStopwords() {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(STOPWORDS_FILE));
      String line = null;
      while ((line = reader.readLine()) != null) {
        _stopwords.add(line.trim());
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.print("Stopword file : " + STOPWORDS_FILE + " not found");
    } catch (IOException e) {
      System.out.print("Failed to read stopwords from " + STOPWORDS_FILE + ": " + e.getMessage());
    }
  }

  @Override
  public void processQuery() {
    if (_query == null) {
      return;
    }

    Scanner s = new Scanner(_query);
    while(s.hasNext()) {
      List<String> words = _segmentor.parse(s.next().trim());
      for (String word : words) {
        if (!_tokens.contains(word) &&
            ((words.size() >= 2 && !_stopwords.contains(word)) ||
            words.size() < 2 )) {
          _tokens.add(word);
        }
      }
    }
  }

  public static void main(String[] args) {
    ChineseSegmentor segmentor = new ChineseSegmentor();
    String q = "我们的";
    QueryChinese qc = new QueryChinese(q, segmentor);
    qc.processQuery();
    Helper.printVerbose(Arrays.toString(qc._tokens.toArray()));
  }
}
