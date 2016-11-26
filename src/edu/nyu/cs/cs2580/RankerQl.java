package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Use this template to implement the query likelihood ranker for HW1.
 * 
 * @author congyu
 * @author fdiaz
 */
public class RankerQl extends Ranker {

  public static final double LAMBDA = 0.5;

  public RankerQl(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> all = new Vector<ScoredDocument>();
    for (int i = 0; i < _indexer.numDocs(); ++i) {
      all.add(scoreDocument(query, i));
    }
    Collections.sort(all, Collections.reverseOrder());
    Vector<ScoredDocument> results = new Vector<ScoredDocument>();
    for (int i = 0; i < all.size() && i < numResults; ++i) {
      results.add(all.get(i));
    }
    return results;
  }

  private ScoredDocument scoreDocument(Query query, int did) {
    Document doc = _indexer.getDoc(did);
    double score = scoreDocument(query, doc);

    return new ScoredDocument(doc, score);
  }

  public double scoreDocument(Query query, Document doc) {
    Vector<String> queryTokens = query._tokens;
    Map<String, Integer> queryVec = getVec(queryTokens);

    Vector<String> docTokens = ((DocumentFull) doc).getConvertedBodyTokens();
    Map<String, Integer> docVec = getVec(docTokens);

    Map<String, Integer> docTermFrequencyVec = new HashMap<>();
    for (String key : queryVec.keySet()) {
      if (docVec.containsKey(key)) {
        docTermFrequencyVec.put(key, docVec.get(key));
      } else {
        docTermFrequencyVec.put(key, 0);
      }
    }

    long corpusTotalTerm = _indexer.totalTermFrequency();
    long docTotalTerm = docTokens.size();
    double score = 0.0;
    for (String key : docTermFrequencyVec.keySet()) {
      int corpusTermFrequency = _indexer.corpusTermFrequency(key);
      score += Math.log((1 - LAMBDA) * docTermFrequencyVec.get(key) / docTotalTerm +
              LAMBDA * _indexer.corpusTermFrequency(key) / corpusTotalTerm);
    }

    return score;
  }

  private Map<String,Integer> getVec(Vector<String> tokens) {
    Map<String, Integer> vec = new HashMap<>();
    for (String token : tokens) {
      if (!vec.containsKey(token)) {
        vec.put(token, 1);
      } else {
        vec.put(token, vec.get(token) + 1);
      }
    }

    return vec;
  }
}
