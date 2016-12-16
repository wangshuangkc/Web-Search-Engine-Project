package edu.nyu.cs.cs2580;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Vector;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.nyu.cs.cs2580.SearchEngine.Options;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Handles each incoming query, students do not need to change this class except
 * to provide more query time CGI arguments and the HTML output.
 * 
 * N.B. This class is not thread-safe. 
 * 
 * @author congyu
 * @author fdiaz
 */
class QueryHandler implements HttpHandler {

  /**
   * CGI arguments provided by the user through the URL. This will determine
   * which Ranker to use and what output format to adopt. For simplicity, all
   * arguments are publicly accessible.
   */
  public static class CgiArguments {
    // The raw user query
    public String _query = "";
    // How many results to return
    private int _numResults = 10;
    private int _numDocs = 0;
    private int _numTerms = 0;
    
    // The type of the ranker we will be using.
    public enum RankerType {
      NONE,
      FULLSCAN,
      CONJUNCTIVE,
      FAVORITE,
      COSINE,
      PHRASE,
      QL,
      LINEAR,
      COMPREHENSIVE,
    }
    public RankerType _rankerType = RankerType.COMPREHENSIVE;
    
    // The output format.
    public enum OutputFormat {
      TEXT,
      HTML,
    }
    public OutputFormat _outputFormat = OutputFormat.TEXT;

    public CgiArguments(String uriQuery) {
      String[] params = uriQuery.split("&");
      for (String param : params) {
        String[] keyval = param.split("=", 2);
        if (keyval.length < 2) {
          continue;
        }
        String key = keyval[0].toLowerCase();
        String val = keyval[1];
        if (key.equals("query")) {
          try {
            _query = URLDecoder.decode(val, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }
        } else if (key.equals("num")) {
          try {
            _numResults = Integer.parseInt(val);
          } catch (NumberFormatException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("ranker")) {
          try {
            _rankerType = RankerType.valueOf(val.toUpperCase());
          } catch (IllegalArgumentException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("format")) {
          try {
            _outputFormat = OutputFormat.valueOf(val.toUpperCase());
          } catch (IllegalArgumentException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("numdocs")) {
          try {
            _numDocs = Integer.parseInt(val);
          } catch (NumberFormatException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("numterms")) {
          try {
            _numTerms = Integer.parseInt(val);
          } catch (NumberFormatException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        }
      }  // End of iterating over params
    }
  }

  // For accessing the underlying documents to be used by the Ranker. Since 
  // we are not worried about thread-safety here, the Indexer class must take
  // care of thread-safety.
  private Indexer _indexer;

  public QueryHandler(Options options, Indexer indexer) {
    _indexer = indexer;
  }

  private void respondWithMsg(HttpExchange exchange, final String message)
      throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    responseHeaders.set("Access-Control-Allow-Origin", "*");
    responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
    exchange.sendResponseHeaders(200, 0); // arbitrary number of bytes
    OutputStream responseBody = exchange.getResponseBody();
    responseBody.write(message.getBytes());
    responseBody.close();
  }

  private void constructTextOutput(
      final Vector<ScoredDocument> docs, StringBuffer response) {
    /*if (docs.size() == 0) {
      response.append("No results");
    }
    response.append("URL\tTitle\tSpeaker\n");
    for (ScoredDocument doc : docs) {
      response.append(response.length() > 0 ? "\n" : "");
      response.append(doc.asTextResult());
    }
    response.append(response.length() > 0 ? "\n" : ""); */
    JSONObject results =new JSONObject();
    int i = 1;
    for (ScoredDocument doc : docs) {
      VideoDocumentIndexed video = (VideoDocumentIndexed)doc.getDoc();
      JSONObject obj = new JSONObject();
      obj.put("url", video.getUrl());
      obj.put("title", video.getTitle());
      obj.put("speaker", video.getSpeaker());
      results.put(i++, obj);
    }
    response.append(results.toJSONString());
  }

  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    if (!requestMethod.equalsIgnoreCase("GET")) { // GET requests only.
      return;
    }

    // Print the user request header.
    Headers requestHeaders = exchange.getRequestHeaders();
    System.out.print("Incoming request: ");
    for (String key : requestHeaders.keySet()) {
      System.out.print(key + ":" + requestHeaders.get(key) + "; ");
    }
    System.out.println();

    // Validate the incoming request.
    String uriQuery = exchange.getRequestURI().getQuery();
    String uriPath = exchange.getRequestURI().getPath();
    if (uriPath == null || uriQuery == null) {
      respondWithMsg(exchange, "Something wrong with the URI!");
    }
    if (!uriPath.equals("/search") && !uriPath.equals("/prf")) {
      respondWithMsg(exchange, "Only /search or /prf is handled!");
    }
    System.out.println("Query: " + uriQuery);

    // Process the CGI arguments.
    CgiArguments cgiArgs = new CgiArguments(uriQuery);
    if (cgiArgs._query.isEmpty()) {
      respondWithMsg(exchange, "No query is given!");
    }
    
    // Create the ranker.
    Ranker ranker = Ranker.Factory.getRankerByArguments(
        cgiArgs, SearchEngine.OPTIONS, _indexer);
    if (ranker == null) {
      respondWithMsg(exchange,
          "Ranker " + cgiArgs._rankerType.toString() + " is not valid!");
    }

    // Processing the query.
    Query processedQuery = new Query(cgiArgs._query);
    processedQuery.processQuery();
    
    StringBuffer response = new StringBuffer();
    
    // Ranking.
    if (uriPath.equals("/search")) {
      Vector<ScoredDocument> scoredDocs =
              ranker.runQuery(processedQuery, cgiArgs._numResults);
      switch (cgiArgs._outputFormat) {
        case TEXT:
          constructTextOutput(scoredDocs, response);
          respondWithMsg(exchange, response.toString());
          break;
        case HTML:
          break;
        default:
          break;
      }
    } else {
      System.out.println("start prf");
      Vector<ScoredDocument> scoredDocs =
          ranker.runQuery(processedQuery, cgiArgs._numDocs);
      PRF prf = new PRF(scoredDocs, cgiArgs._numTerms, _indexer);
      System.out.println("start construct response");
      prf.constructResponse(response);
      System.out.println(response.toString());
      respondWithMsg(exchange, response.toString());
    }

    System.out.println("Finished query: " + cgiArgs._query);
  }

  private void constructHtmlOutput(Vector<ScoredDocument> docs, StringBuffer response) throws IOException {
    JSONArray result = new JSONArray();
    for (ScoredDocument doc : docs) {
      VideoDocumentIndexed video = (VideoDocumentIndexed)doc.getDoc();
      JSONObject obj = new JSONObject();
      obj.put("url", video.getUrl());
      obj.put("title", video.getTitle());
      obj.put("speaker", video.getSpeaker());
      result.add(obj);
    }
    response.append(result);
  }
}

