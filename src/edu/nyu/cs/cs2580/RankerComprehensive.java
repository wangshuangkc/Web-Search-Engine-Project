package edu.nyu.cs.cs2580;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3 based on your {@code RankerFavorite}
 * from HW2. The new Ranker should now combine both term features and the
 * document-level features including the PageRank and the NumViews. 
 */
public class RankerComprehensive extends Ranker {
  private double _lambda = 0.5;

  public RankerComprehensive(Options options,
       CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> results = null;
    Helper.printVerbose("start querying");
    try {
      QueryChinese qc = new QueryChinese(query._query, ((IndexerInverted) _indexer)._segmentor);
      qc.processQuery();
      Vector<ScoredDocument> scoredDocs = new Vector<>();
      VideoDocumentIndexed doc = (VideoDocumentIndexed) _indexer.nextDoc(qc, -1);
      Helper.printVerbose("find potential doc: " + doc._docid);
      while (doc != null) {
        scoredDocs.add(scoreDocument(qc, doc._docid));
        doc = (VideoDocumentIndexed) _indexer.nextDoc(qc, doc._docid);
      }
  
      Collections.sort(scoredDocs, new Comparator<ScoredDocument>() {
        @Override
        public int compare(ScoredDocument doc1, ScoredDocument doc2) {
          return doc2.compareTo(doc1);
        }
      });
      
      results = new Vector<>();
      int cnt = numResults;
      Helper.printVerbose("scored docs all: " + scoredDocs.size());
      for (ScoredDocument sDoc : scoredDocs) {
//        if (cnt == 0) {
//          break;
//        }
        results.add(sDoc);
        cnt--;
      }
      Helper.printVerbose("scored docs: " + results.size());
    } catch (Exception e) {
      Helper.printVerbose("Error: ranking failed");
      e.printStackTrace();
    }
    
    return results;
  }
  
  private ScoredDocument scoreDocument(Query query, int docid) {
    VideoDocumentIndexed doc = (VideoDocumentIndexed)_indexer.getDoc(docid);
    long docTotalTerm = doc.getDocTotalTerms();
    if (doc == null) {
      Helper.printVerbose("No document with Id: " + docid);
      return null;
    }
    
    float relevance = 0.0f;
    for (String token : query._tokens) {
      int tf = _indexer.documentTermFrequency(token, docid);
      ;
      relevance += Math.log((1 - _lambda) * tf / docTotalTerm +
          _lambda * _indexer.corpusTermFrequency(token) / _indexer._totalTermFrequency);
    }
    relevance = (float)Math.pow(10, relevance);
    if (relevance == 0) {
      return null;
    }

    Helper.printVerbose("relevance: " + relevance);

    int numview = doc.getNumViews() / Helper.postPeriod(doc.getPostMonths());
    
    double score = 0.75 * relevance + 0.25 * Math.log(numview + 1);
    
    return new ScoredDocument(doc, score);
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Options op = new Options("conf/engine.conf");
    CgiArguments arg = new CgiArguments("query=权利%20恐怖");
    Indexer idx = new IndexerInverted(op);
    idx.loadIndex();
    RankerComprehensive ranker = new RankerComprehensive(op, arg, idx);
    Vector<ScoredDocument> sdoc = ranker.runQuery(new Query(arg._query), 0);
    for (ScoredDocument d : sdoc) {
      Document doc = d.getDoc();
      Helper.printVerbose(doc.getUrl());
    }
  }

  public double getLambda() {
    return _lambda;
  }
  
  public void setLambda(double lambda) {
    _lambda = lambda;
  }
}

