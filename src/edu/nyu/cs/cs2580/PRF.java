package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * Created by kc on 11/18/16.
 */
public class PRF {
  private int _numTerms;
  private Vector<ScoredDocument> _scoredDocs;
  private final Indexer _indexer;
  private Map<String, Float> termProbability = new HashMap<String, Float>();
  private Map<String, Integer> termFrequency = new HashMap<String, Integer>();

  public PRF(Vector<ScoredDocument> scoredDocs, int numTerms, Indexer indexer) {
    _numTerms = numTerms;
    _scoredDocs = scoredDocs;
    _indexer = indexer;
  }
  
  /**
   * Calculate the probability for a term
   * w: term
   * D: _scoredDocs
   * get a Map<TermId, Frequence> for unique terms of DocumentIndexed doc: doc.getUniqueTerms()
   * getUniqueTerms() may not be allowed to use
   * @param term
   * @return
   */
  float totalWordCount = 0;
  float frequency = 0;
  float probability = 0;
  float totalProbability = 0;
  float normalizedProbability = 0;

  private float calculateProb(String term) {

    for(ScoredDocument document : _scoredDocs){
      totalWordCount += ((DocumentIndexed)document.getDoc()).getDocTotalTerms();
    }
    frequency = termFrequency.get(term);
    probability = frequency / totalWordCount;
    
    return probability;
  }


  public void constructResponse(StringBuffer response) throws IOException {
    int temp = 0;
    int sum = 0;
    //Construct a map of unique terms with initial value of 0;
    for(String term: ((IndexerInvertedCompressed)_indexer).getUniqTerms()) {
      for(ScoredDocument document : _scoredDocs){
          int docid = ((DocumentIndexed)document.getDoc())._docid;
          temp=((IndexerInvertedCompressed)_indexer).documentTermFrequency(term, docid);
          sum += temp;
      }
      if(sum>0){
        termFrequency.put(term, sum);
      }
    }

    //Calculate probablity of each term in the map, update map.
    for (String term : termFrequency.keySet()) {
      termProbability.put(term,calculateProb(term));
    }

    //Sort map.
    sortMapByValue(termProbability);

    //Renormalize probability and save them.
    List<String> keys = new ArrayList<String>(termProbability.keySet());

    for (int i = 0; i <_numTerms ; i++) {
      totalProbability += termProbability.get(keys.get(i));
    }
    
    for (int i = 0; i <_numTerms ; i++) {
      normalizedProbability = termProbability.get(keys.get(i)) / totalProbability;
      response.append(keys.get(i) + "\t" + normalizedProbability+"\n");
    }
    
    String outFile = "data/output/prfOutput.tsv";
    File output = new File(outFile);
    FileWriter writer = new FileWriter(output);
    writer.write(response.toString());
    writer.close();
  }

  public Map<String, Float> sortMapByValue(Map<String, Float> map) {
  
    List<Map.Entry<String, Float>> list = new LinkedList<>(map.entrySet());
  
    Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
      @Override
      public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    });
  
    Map<String, Float> sortedMap = new HashMap<String, Float>();
    for (Map.Entry<String, Float> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }
  
    return sortedMap;
  }
  
  public static void main(String[] args) throws IOException {
    SearchEngine.Options op = new SearchEngine.Options("conf/engine.conf");
    String corpusPath = op._corpusPrefix;
    File corpusDir = new File(corpusPath);
    
    for (File corpusDoc : corpusDir.listFiles()) {
      if (CorpusAnalyzer.isValidDocument(corpusDoc)) {
        //numViews.put(convertToUTF8(corpusDoc.getName()), 0);
      }
    }
  }
}
