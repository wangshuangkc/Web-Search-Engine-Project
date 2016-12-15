package edu.nyu.cs.cs2580;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Extracts video info and transcripts, and stores in a json object
 * Indexer can tokenize the data, and index the page
 *
 * @author Shuang
 */
public class TedExtractor {
  private final String _url;
  private static final String INFO_URL = "?language=zh-cn";
  private static final String TRAN_URL = "/transcript" + INFO_URL;
  private static final String TITLE_SELECTOR = ".player-hero__title__content";
  private static final String NAME_SELECTOR = ".talk-speaker__name";
  private static final String SHARED_SELECTOR = "#sharing-count";
  private static final String TIME_SELECTOR_META = ".meta__item";
  private static final String TIME_SELECTOR = ".meta__val";
  private static final String DEC_SELECTOR = ".talk-description";
  private static final String TRAN_SELECTOR = "p.talk-transcript__para";
  private static final String TRAN_TIME_SELECTOR = "data.talk-transcript__para__time";
  private static final String PARA_SELECTOR = "span.talk-transcript__para__text";

  public TedExtractor(String url) {
    _url = url;
  }

  /**
   * Extracts video title, speaker name, total shared, and descriptions from the
   * @return json object storing video data
   * @throws IOException
   */
  public JSONObject extract() throws IOException {
    JSONObject obj = new JSONObject();

    Document info = Jsoup.connect(_url + INFO_URL).get();
    obj.put("title", extractTitle(info));
    obj.put("speaker", extractSpeaker(info));
    obj.put("shared", extractNumShared(info));
    obj.put("description", extractDescription(info));

    Document trans = Jsoup.connect(_url + TRAN_URL).get();
    obj.put("time", extractTime(trans));
    obj.put("transcript", extractTranscript(trans));

    return obj;
  }

  private String extractTitle(Document doc) {
    return doc.select(TITLE_SELECTOR).text();
  }

  private String extractSpeaker(Document doc) {
    return doc.select(NAME_SELECTOR).text();
  }

  private String extractNumShared(Document doc) {
    String num = doc.select(SHARED_SELECTOR).text();
    String[] n = num.split(" ");

    return n[0].replace(",", "");
  }

  private String extractTime(Document doc) {
    Elements time = doc.select(TIME_SELECTOR_META);
    String t = time.select(TIME_SELECTOR).text();

    return t;
  }

  private String extractDescription(Document doc) {
    return doc.select(DEC_SELECTOR).text();
  }

  private String extractTranscript(Document doc) {
    StringBuffer sb = new StringBuffer();
    Elements trans = doc.select(TRAN_SELECTOR);
    for (Element tran : trans) {
      String timeTag = tran.select(TRAN_TIME_SELECTOR).text();
      String para = tran.select(PARA_SELECTOR).text();
      sb.append(timeTag.replaceAll(" ", "") + "\t");
      sb.append(para.replaceAll(" ", "") + "\n");
    }

    return sb.toString();
  }

  public static void main(String[] args) throws IOException, ParseException {
    String urlLists = "data/web/cached_urls.json";
    System.out.println("Read cached last url from " + urlLists);
    File cachedFile = new File(urlLists);
    if (cachedFile.exists()) {
      JSONParser parser = new JSONParser();
      JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(cachedFile));
      String base_url = jsonObj.get("base_url").toString();
      JSONArray urls = (JSONArray) jsonObj.get("video_url");
      Iterator i = urls.iterator();
      int cnt = 0;
      while (i.hasNext() && cnt <= 10) {
        cnt++;
        String vurl = (String)i.next();
        TedExtractor tex = new TedExtractor(base_url + vurl);
        FileWriter writer = new FileWriter("data/tran" + cnt + ".json");
        writer.write(tex.extract().toString());
        writer.close();
        Helper.printVerbose("Parse url #" + cnt);
      }
    }
  }
}