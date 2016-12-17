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
    Map<Integer, Double> relevances = new HashMap<Integer, Double>();

    public DocumentRelevances() { }

    public void addDocument(int docid, String grade) {
      relevances.put(docid, convertToBinaryRelevance(grade));
    }

    public boolean hasRelevanceForDoc(int docid) {
      return relevances.containsKey(docid);
    }

    public double getRelevanceForDoc(int docid) {
      return relevances.get(docid);
    }

    private static double convertToBinaryRelevance(String grade) {
      if (grade.equalsIgnoreCase("Perfect")) {
        return 10;
      }
      if (grade.equalsIgnoreCase("Excellent")) {
        return 7;
      }
      if (grade.equalsIgnoreCase("Good")) {
        return 5;
      }
      if (grade.equalsIgnoreCase("Fair")) {
        return 1;
      }

      return 0;
    }
  }

  /**
   * Usage: java -cp src edu.nyu.cs.cs2580.Evaluator [labels] [metric_id]
   */
  public static void main(String[] args) throws IOException {
    Map<String, DocumentRelevances> judgements =
        new HashMap<String, DocumentRelevances>();
    Vector<String> rankedResults = new Vector<String>();

    SearchEngine.Check(args.length == 3, "Must provide labels and metric_id!");
    readRelevanceJudgments(args[0], judgements);
    evaluateStdin(Integer.parseInt(args[2]), judgements, args[1]);

  }

  public static void readRelevanceJudgments(
      String judgeFile, Map<String, DocumentRelevances> judgements)
      throws IOException {
    File text = new File(judgeFile);
    Scanner scan = new Scanner(text);

    while (scan.hasNext()) {
      String curLine = scan.nextLine();
      String[] splitted = curLine.split("\t");
      String query = splitted[0];
      DocumentRelevances relevances = judgements.get(query);
      if (relevances == null) {
        relevances = new DocumentRelevances();
        judgements.put(query, relevances);
      }
<<<<<<< HEAD
      relevances.addDocument(splitted[1], splitted[2]);
=======
      relevances.addDocument(Integer.parseInt(s.next()), s.next());
>>>>>>> master
      judgements.put(query, relevances);
    }
    scan.close();

  }

  // @CS2580: implement various metrics inside this function
  public static void evaluateStdin(
      int metric, Map<String, DocumentRelevances> judgments)
      throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in));
    List<Integer> results = new ArrayList<Integer>();
    String line = null;
    String currentQuery = "";
    while ((line = reader.readLine()) != null) {
      Scanner s = new Scanner(line).useDelimiter("\t");
      final String query = s.next();
      if (!query.equals(currentQuery)) {
        if (results.size() > 0) {
          StringBuffer sb = new StringBuffer();
          int[] ks = {10, 50, 100};
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
              double[] rs = new double[11];
              for (int i = 0; i < 11; i++) {
                rs[i] = i / 10;
              }
              double[] result = evaluateQueryMetric3(currentQuery, results, judgments, rs);
              for (Double d : result) {
                sb.append(d + " ");
              }
              break;
            case 4:
              sb.append(evaluateQueryMetric4(currentQuery, results, judgments));
              break;
            case 5:
              for (int k : ks) {
                sb.append(evaluateQueryMetric5(currentQuery, results, judgments, k) + ",");
              }
              sb.deleteCharAt(sb.length() - 1).append("\t");
              break;
            case 6:
              sb.append(evaluateQueryMetric6(currentQuery, results, judgments));
              break;
            default:
              System.err.println("Requested metric not implemented!");
          }
          results.clear();
        }
        currentQuery = query;
      }
      results.add(Integer.parseInt(s.next()));
      s.close();
    }
    reader.close();
    if (results.size() > 0) {
      StringBuffer sb = new StringBuffer(currentQuery + "\t");
      int[] ks = {10, 50, 100};
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
          double[] rs = new double[11];
          for (int i = 0; i < 11; i++) {
            rs[i] = i / 10;
          }
          double[] result = evaluateQueryMetric3(currentQuery, results, judgments, rs);
          for (Double d : result) {
            sb.append(d + " ");
          }
          break;
        case 4:
          sb.append(evaluateQueryMetric4(currentQuery, results, judgments));
          break;
        case 5:
          for (int k : ks) {
            sb.append(evaluateQueryMetric5(currentQuery, results, judgments, k) + ",");
          }
          sb.deleteCharAt(sb.length() - 1).append("\t");
          break;
        case 6:
          sb.append(evaluateQueryMetric6(currentQuery, results, judgments));
          break;
        default:
          System.err.println("Requested metric not implemented!");
      }
      System.out.println(sb.toString());
    }
  }

  private static double evaluateQueryMetric0(String query, List<Integer> docids,
                                             Map<String, DocumentRelevances> judgments, int k){
    double RR = 0.0;
    DocumentRelevances relevances = judgments.get(query);
    if(relevances == null){
      System.out.println("Query [" + query + "] not found!");
    } else {
      for (int i = 0; i < k; i++) {
        if (relevances.hasRelevanceForDoc(docids.get(i)) && relevances.getRelevanceForDoc(docids.get(i)) >= 5) {
          RR += 1;
        }
      }
    }

    if (k == 0) {
      return RR;
    }

    return RR / k;
  }

  private static double evaluateQueryMetric1(String query, List<Integer> docids,
                                             Map<String, DocumentRelevances> judgments, int k){
    int countR = 0;
    DocumentRelevances relevances = judgments.get(query);
    for (Map.Entry<Integer, Double> entry : relevances.relevances.entrySet()) {
      if (entry.getValue() >= 5) {
        countR++;
      }
    }
    double RR = 0.0;
    for (int i = 0; i < k; i++) {
      if (relevances.hasRelevanceForDoc(docids.get(i)) && relevances.getRelevanceForDoc(docids.get(i)) >= 5) {
        RR = RR + 1;
      }
    }

    if (countR == 0) {
      return RR;
    }

    return RR/countR;
  }

  private static double evaluateQueryMetric2(String query, List<Integer> docids,
                                             Map<String, DocumentRelevances> judgments, int k) {
    double alpha = 0.5;
    double p = evaluateQueryMetric0(query, docids, judgments, k);
    double r = evaluateQueryMetric1(query, docids, judgments, k);
    return 1 / (alpha / p + (1 - alpha) / r);
  }

  private static double[] evaluateQueryMetric3(String query, List<Integer> docids,
                                             Map<String, DocumentRelevances> judgements, double[] rScores){
    double result = 0;

    List<Double> ps = new ArrayList<>();
    List<Double> rs = new ArrayList<>();
    for(int i = 0; i < docids.size(); i++) {
      ps.add(evaluateQueryMetric0(query, docids, judgements, i + 1));
      rs.add(evaluateQueryMetric1(query, docids, judgements, i + 1));
      System.out.println("p / r: " + ps.get(i) + " / " + rs.get(i));
    }

    double[] results = new double[rScores.length];
    for (int i = 0; i < rScores.length; i++) {
      Double r = rScores[i];
      results[i] = getMaxP(ps, rs, i);
    }

    return results;
  }

  private static double getMaxP(List<Double> ps, List<Double> rs, int r) {
    double max = 0;
    for (int i = 0; i < rs.size(); i++) {

      if (rs.get(i) * 10 >= r) {
        max = Math.max(max, ps.get(i));
      }
      System.out.println("rscore: " + rs.get(i) + " > " + r + " : " + ps.get(i) + " | " + max);
    }

    return max;
  }

  public static double evaluateQueryMetric4(String query, List<Integer> docids,
                                            Map<String, DocumentRelevances> judgments) {
    int position= 0;
    double R = 0.0;
    double result = 0.0;


    for (int docid : docids) {
      DocumentRelevances relevances = judgments.get(query);
      position++;
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docid) && relevances.getRelevanceForDoc(docid) >= 5) {
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

  public static double evaluateQueryMetric5(String query, List<Integer> docids,
                                            Map<String, DocumentRelevances> judgments, int k) {
    DocumentRelevances relevances = judgments.get(query);
    double result = 0;
    if (relevances == null) {
      System.out.println("Query [" + query + "] not found!");

      return 0;
    }
    List<Double> rels = new ArrayList<>();
    for (int i = 0; i < k; i++) {
      int docid = docids.get(i);
      if (relevances.hasRelevanceForDoc(docid)) {
        rels.add(relevances.getRelevanceForDoc(docid));
      } else {
        rels.add(0d);
      }
    }
    List<Double> sorted = new ArrayList<>();
    sorted.addAll(rels);
    Collections.sort(sorted, Collections.reverseOrder());
    double dcg = 0;
    double idcg = 0;
    for (int i = 1; i <= k; i++) {
      dcg += rels.get(i - 1) / (Math.log(i + 1) / Math.log(2));
      idcg += sorted.get(i - 1) / (Math.log(i + 1) / Math.log(2));
    }
    if (idcg == 0) {
      return 0;
    }

    return dcg / idcg;
  }

  private static double evaluateQueryMetric6(String query, List<Integer> docids,
                                             Map<String, DocumentRelevances> judgments) {
    double RR = 0.0;
    for (int i = 0; i <= docids.size(); i++) {
      DocumentRelevances relevances = judgments.get(query);
      if (relevances == null) {
        System.out.println("Query [" + query + "] not found!");
      } else {
        if (relevances.hasRelevanceForDoc(docids.get(i)) && relevances.getRelevanceForDoc(docids.get(i)) >= 5) {
          RR = 1.0 / (i + 1);
          break;
        }
      }
    }
    return RR;
  }

  public static void evaluateQueryInstructor(
      String query, List<Integer> docids,
      Map<String, DocumentRelevances> judgments) {
    double R = 0.0;
    double N = 0.0;
    for (int docid : docids) {
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
