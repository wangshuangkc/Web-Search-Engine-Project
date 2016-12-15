package edu.nyu.cs.cs2580;

import java.io.*;
import java.util.*;

import edu.nyu.cs.cs2580.SearchEngine.Options;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


/**
 * Crawler object for getting video urls
 *
 * @author Shuang
 */
public class TedCrawler implements Serializable {
  private Options _options = null;
  private static final String BASE_URL = "https://www.ted.com";
  private static final String[] LANGUAGES = {"zh-cn"};
  private long totalPage = 4; //todo change back to 65
  private static final String CACHED_LAST = "/cached_last.json";
  private static final String CACHED_URLS = "/cached_urls.json";
  private Map<String, String> lastCachedUrl = new HashMap<>();

  public TedCrawler(Options options) {
    _options = options;
  }

  /**
   * Crawls the links on ted.com/talks page, and stores into a json file
   * With caching last crawled url, only add urls that were not crawled for freshing
   *
   * @throws IOException
   * @throws ParseException
   */
  public String graspUrls() throws IOException, ParseException {
    System.out.println("Grasp urls from: " + BASE_URL);
    String webDir = _options._webPrefix;
    File webDirFile = new File(webDir);
    if (!webDirFile.exists()) {
      webDirFile.mkdirs();
    }

    List<String> urls = new ArrayList<>();
    for (String lang : LANGUAGES) {
      Helper.printVerbose("Download page for " + lang);
      for (String s : urlsForLang(lang)) {
        if (!urls.contains(s)) {
          urls.add(s);
        }
      }
    }

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("base_url", BASE_URL);
    jsonObject.put("video_url", urls);

    String urlFile = _options._webPrefix + CACHED_URLS;
    FileWriter file = new FileWriter(urlFile);
    file.write(jsonObject.toString());
    file .close();

    writeLastCached();
    System.out.println("Url Crawling completed: " + urls.size() + " urls stored to " + urlFile);

    return urlFile;
  }

  private List<String> urlsForLang(String lang) throws IOException, ParseException {
    List<String> urls = new ArrayList<>();
    readLastCached();

    boolean reachCachedLast = false;
    for (int i = 1; i <= totalPage && !reachCachedLast; i++) {
      Helper.printVerbose("Download page # " + i + " ...");
      Document doc = Jsoup.connect(BASE_URL + "/talks?language=" + lang + "&page=" + i).get();
      if (doc.toString().contains("Sorry. We couldn't find a talk quite like that.")) {
        Helper.printVerbose("no next page");
        totalPage = Math.max(totalPage, i - 1);
        break;
      }

      Elements links = doc.select("[href~=^(?i)/talks/.*=" + lang + "]");

      for (int j = 0; j < links.size(); j++) {
        String link = links.get(j).attr("href");
        if (link != null) {
          link = link.replaceFirst("[\\?|&].*", "");
          if (link.equals(lastCachedUrl.get(lang))) {
            reachCachedLast = true;
            totalPage += i - 1;
            break;
          } else if (!urls.contains(link)) {
            urls.add(link);
          }
        }
      }
    }
    if (!urls.isEmpty()) {
      lastCachedUrl.put(lang, urls.iterator().next());
    }
    return urls;
  }

  private void readLastCached() throws IOException, ParseException {
    String cachedFileName = _options._webPrefix + CACHED_LAST;
    System.out.println("Read cached last url from " + cachedFileName);
    File cachedFile = new File(cachedFileName);
    if (cachedFile.exists()) {
      JSONParser parser = new JSONParser();
      JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(cachedFileName));
      for (String lang : LANGUAGES) {
        String lastUrl = (String) jsonObj.get(lang);
        lastCachedUrl.put(lang, lastUrl);
      }
      totalPage = (long)jsonObj.get("total_page");
    }
  }

  private void writeLastCached() throws IOException {
    String cachedFileName = _options._webPrefix + CACHED_LAST;
    JSONObject jsonObj = new JSONObject();
    jsonObj.put("total_page", totalPage);
    for (Map.Entry<String, String> entry : lastCachedUrl.entrySet()) {
      jsonObj.put(entry.getKey(), entry.getValue());
    }
    FileWriter file = new FileWriter(cachedFileName);
    file.write(jsonObj.toString());
    file.close();

    Helper.printVerbose("Write last url to " + cachedFileName);
  }

  public static void main(String[] args) throws IOException, ParseException {
    Options options = new Options("conf/engine.conf");
    TedCrawler crawler = new TedCrawler(options);
    crawler.graspUrls();
  }
}
