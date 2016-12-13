
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


/** This is a very simple demo of calling the Chinese Word Segmenter
 *  programmatically.  It assumes an input file in UTF8.
 *  <p/>
 *  <code>
 *  Usage: java -mx1g -cp seg.jar SegChinese fileName
 *  </code>
 *  This will run correctly in the distribution home directory.  To
 *  run in general, the properties for where to find dictionaries or
 *  normalizations have to be set.
 *
 *  @author Christopher Manning
 */

public class SegChinese {
    private static final String basedir = System.getProperty("SegChinese", "/Users/WeisenZhao/Downloads/stanford-segmenter-2015-12-09/data");
    private CRFClassifier<CoreLabel> segmenter;

    public SegChinese(){
        Properties props = new Properties();
        props.setProperty("sighanCorporaDict", basedir);
        props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
        props.setProperty("inputEncoding", "UTF-8");
        props.setProperty("sighanPostProcessing", "true");
        segmenter = new CRFClassifier<>(props);
        segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
    }
    //Return the segmented Chinese
    public List<String> segmentedChinese(String sentence){
        List<String> segmented = segmenter.segmentString(sentence);
        return segmented;
    }
}
