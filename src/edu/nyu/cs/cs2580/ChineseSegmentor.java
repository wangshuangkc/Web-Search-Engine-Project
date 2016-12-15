package edu.nyu.cs.cs2580;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


/**
 * Segment Chinese sentence into valid words
 * @link http://nlp.stanford.edu/software/segmenter.shtml
 * @author Weisen
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

  public List<String> parse(String sentence) {
    List<String> segmented = segmenter.segmentString(sentence);
    return segmented;
  }

  public static void main(String[] args) {
    ChineseSegmentor seg = new ChineseSegmentor();
    String s = "中国经济";
    Helper.printVerbose(Arrays.toString(seg.parse(s).toArray()));
  }
}
