package edu.nyu.cs.cs2580;

import java.io.*;
import java.util.*;

/**
 * Evaluator for HW1.
 *
 * @author fdiaz
 * @author congyu
 */
class Evaluator {
  static class DocumentRelevances {
    Map<String, Double> relevances = new HashMap<String, Double>();

    public DocumentRelevances() { }

    public void addDocument(String title, String grade) {
      relevances.put(title, convertToBinaryRelevance(grade));
    }

    public boolean hasRelevanceForDoc(String title) {
      return relevances.containsKey(title);
    }

    public double getRelevanceForDoc(String title) {
      return relevances.get(title);
    }

    private static double convertToBinaryRelevance(String grade) {
      if (grade.equalsIgnoreCase("Perfect")) {
        return 1.0;
      }
      if (grade.equalsIgnoreCase("Excellent")) {
        return 1.0;
      }
      if (grade.equalsIgnoreCase("Good")) {
        return 1.0;
      }
      if (grade.equalsIgnoreCase("Fair")) {
        return 1.0;
      }

      return 0.0;
    }
  }

  /**
   * Usage: java -cp src edu.nyu.cs.cs2580.Evaluator [labels] [rankedResults] [metric_id]
   */
  public static void main(String[] args) throws IOException {
    Map<String, DocumentRelevances> judgments =
        new HashMap<String, DocumentRelevances>();
    Vector<String> rankedResults = new Vector<String>();

    SearchEngine.Check(args.length == 2, "Must provide labels and metric_id!");
    readRelevanceJudgments(args[0], judgments);
    evaluateStdin(Integer.parseInt(args[2]), judgments, args[1]);
  }

  public static void readRelevanceJudgments(
      String judgeFile, Map<String, DocumentRelevances> judgements)
      throws IOException {
    String line = null;
    BufferedReader reader = new BufferedReader(new FileReader(judgeFile));
    while ((line = reader.readLine()) != null) {
      // Line format: query \t docid \t grade
      Scanner s = new Scanner(line).useDelimiter("\t");
      String query = s.next();
      DocumentRelevances relevances = judgements.get(query);
      if (relevances == null) {
        relevances = new DocumentRelevances();
        judgements.put(query, relevances);
      }
      relevances.addDocument(s.next(), s.next());
      judgements.put(query, relevances);
      s.close();
    }
    reader.close();
  }


  // @CS2580: implement various metrics inside this function
  public static void evaluateStdin(
      int metric, Map<String, DocumentRelevances> judgments, String resultFile)
      throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(resultFile));
    List<String> results = new ArrayList<String>();
    String line = null;
    String currentQuery = "";
    while ((line = reader.readLine()) != null) {
      Scanner s = new Scanner(line).useDelimiter("\t");
      final String query = s.next();
      if (!query.equals(currentQuery)) {
        if (results.size() > 0) {
          StringBuffer sb = new StringBuffer();
          int[] ks = {1, 5, 10};
          switch (metric) {
            case -1:
              evaluateQueryInstructor(currentQuery, results, judgments);
              break;
            case 0:
              for (int k : ks) {
                sb.append(evaluateQueryMetric0(currentQuery, results, judgments, k) + ",");
              }
              sb.deleteCharAt(sb.length() - 1).append("\t");
              break;
            case 1:
              for (int k : ks) {
                sb.append(evaluateQueryMetric1(currentQuery, results, judgments, k) + ",");
              }
              sb.deleteCharAt(sb.length() - 1).append("\t");
              break;
            case 2:
              for (int k : ks) {
                sb.append(evaluateQueryMetric2(currentQuery, results, judgments, k) + ",");
              }
              sb.deleteCharAt(sb.length() - 1).append("\t");
              break;
            case 3:
              sb.append(evaluateQueryMetric3(currentQuery, results, judgments));
              break;
            case 4:
              sb.append(evaluateQueryMetric4(currentQuery, results, judgments));
              break;
            default:
              System.err.println("Requested metric not implemented!");
          }
          results.clear();
        }
        currentQuery = query;
      }
      results.add(s.next());
      s.close();
    }
    reader.close();
    if (results.size() > 0) {
      StringBuffer sb = new StringBuffer(currentQuery + "\t");
      int[] ks = {1, 5, 10};
      switch (metric) {
        case -1:
          evaluateQueryInstructor(currentQuery, results, judgments);
          break;
        case 0:
          for (int k : ks) {
            sb.append(evaluateQueryMetric0(currentQuery, results, judgments, k) + ",");
          }
          sb.deleteCharAt(sb.length() - 1).append("\t");
          break;
        case 1:
          for (int k : ks) {
            sb.append(evaluateQueryMetric1(currentQuery, results, judgments, k) + ",");
          }
          sb.deleteCharAt(sb.length() - 1).append("\t");
          break;
        case 2:
          for (int k : ks) {
            sb.append(evaluateQueryMetric2(currentQuery, results, judgments, k) + ",");
          }
          sb.deleteCharAt(sb.length() - 1).append("\t");
          break;
        case 3:
          sb.append(evaluateQueryMetric3(currentQuery, results, judgments));
          break;
        case 4:
          sb.append(evaluateQueryMetric4(currentQuery, results, judgments));
          break;
        default:
          System.err.println("Requested metric not implemented!");
      }
      System.out.println(sb.toString());
    }
  }

  private static double evaluateQueryMetric0(String query, List<String> docids,
                                             Map<String, DocumentRelevances> judgments, int k){
    double RR = 0.0;
    DocumentRelevances relevances = judgments.get(query);
    if(relevances == null){
      System.out.println("Query [" + query + "] not found!");
    } else {
      for (int i = 0; i < k; i++) {
        if (relevances.hasRelevanceForDoc(docids.get(i)) && relevances.getRelevanceForDoc(docids.get(i)) == 1.0) {
          RR += 1;
        }
      }
    }

    if (k == 0) {
      return RR;
    }

    return RR / k;
  }

  private static double evaluateQueryMetric1(String query, List<String> docids,
                                             Map<String, DocumentRelevances> judgments, int k){
    int countR = 0;
    DocumentRelevances relevances = judgments.get(query);
    for (Map.Entry<String, Double> entry : relevances.relevances.entrySet()) {
      if (entry.getValue() == 1.0) {
        countR++;
      }
    }
    double RR = 0.0;
    for (int i = 0; i < k; i++) {
      if (relevances.hasRelevanceForDoc(docids.get(i)) && relevances.getRelevanceForDoc(docids.get(i)) == 1.0) {
        RR = RR + 1;
      }
    }

    if (countR == 0) {
      return RR;
    }

    return RR/countR;
  }

  private static double evaluateQueryMetric2(String query, List<String> docids,
                                             Map<String, DocumentRelevances> judgments, int k) {
    double alpha = 0.5;
    double p = evaluateQueryMetric0(query, docids, judgments, k);
    double r = evaluateQueryMetric1(query, docids, judgments, k);
    return 1 / (alpha / p + (1 - alpha) / r);
  }

  public static double evaluateQueryMetric3(String query, List<String> docids,
                                            Map<String, DocumentRelevances> judgments) {
    int position= 0;
    double R = 0.0;
    double result = 0.0;


    for (String docid : docids) {
      DocumentRelevances relevances = judgments.get(query);
      position++;
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid) && relevances.getRelevanceForDoc(docid) == 1.0) {
          R += 1.0;
          result += R / position;
        }
      }
    }

    if (R == 0) {
      return result;
    }


    return result / R;
  }

  private static double evaluateQueryMetric4(String query, List<String> docids,
                                             Map<String, DocumentRelevances> judgments) {
    double RR = 0.0;
    for (int i = 0; i <= docids.size(); i++) {
      DocumentRelevances relevances = judgments.get(query);
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docids.get(i)) && relevances.getRelevanceForDoc(docids.get(i)) == 1.0) {
          RR = 1.0 / (i + 1);
          break;
        }
      }
    }
    return RR;
  }

  public static void evaluateQueryInstructor(
      String query, List<String> docids,
      Map<String, DocumentRelevances> judgments) {
    double R = 0.0;
    double N = 0.0;
    for (String docid : docids) {
      DocumentRelevances relevances = judgments.get(query);
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid)) {
          R += relevances.getRelevanceForDoc(docid);
        }
        ++N;
      }
    }
    System.out.println(query + "\t" + Double.toString(R / N));
  }
}
