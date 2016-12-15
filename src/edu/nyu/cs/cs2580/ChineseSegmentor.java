package edu.nyu.cs.cs2580;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


/**
 * Segment Chinese sentence into valid words
 * @link http://nlp.stanford.edu/software/segmenter.shtml
 */
public class ChineseSegmentor {
  private static final String basedir = System.getProperty("SegChinese", "data/seg");
  private CRFClassifier<CoreLabel> segmenter;

  public ChineseSegmentor() {
    Properties props = new Properties();
    props.setProperty("sighanCorporaDict", basedir);
    props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
    props.setProperty("inputEncoding", "UTF-8");
    props.setProperty("sighanPostProcessing", "true");
    segmenter = new CRFClassifier<>(props);
    segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
  }

  /**
   * Tokenize a sentence, removing non-Chinese chararacter and punctuations
   * @param sentence
   * @return list of parsed tokens
   */
  public List<String> parse(String sentence) {
    List<String> result = new ArrayList<>();
    List<String> segmented = segmenter.segmentString(sentence);
    for (String token : segmented) {
      if (token == null || token.trim().isEmpty() ||
          isChinesePunctuation(token.charAt(0)) ||
          !isChinese(token))  {
        continue;
      }
      result.add(token);
    }

    return result;
  }

  private boolean isChinesePunctuation(char c) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
        || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
        || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isChinese(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      Character.UnicodeScript sc = Character.UnicodeScript.of(c);
      if (sc != Character.UnicodeScript.HAN) {
        return false;
      }
    }

    return false;
  }

  public static void main(String[] args) {
    ChineseSegmentor seg = new ChineseSegmentor();
    String s = "你们的数据可以帮助终结饥饿问题";
    Helper.printVerbose(Arrays.toString(seg.parse(s).toArray()));
  }
}
