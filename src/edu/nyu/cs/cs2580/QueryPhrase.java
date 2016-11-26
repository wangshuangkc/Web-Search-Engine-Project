package edu.nyu.cs.cs2580;

import java.util.Scanner;
import java.util.Vector;

/**
 * @CS2580: implement this class for HW2 to handle phrase. If the raw query is
 * ["new york city"], the presence of the phrase "new york city" must be
 * recorded here and be used in indexing and ranking.
 */
public class QueryPhrase extends Query {
  
  public QueryPhrase(String query) {
    super(query);
  }
  
  @Override
  public void processQuery() {
    if (_query == null) {
      return;
    }
    
    Scanner s = new Scanner(_query);
    StringBuffer sb = new StringBuffer();
    while (s.hasNext())
    {
      String current = s.next().trim();
      if(current.startsWith("\"")) {
        sb.append(current.replace("\"", ""));
        sb.append(" ");
      } else if(current.endsWith("\"")){
        sb.append(current.replace("\"", ""));
        _tokens.add(sb.toString());
        sb = new StringBuffer();
      } else {
        if(sb.length() == 0) {
          _tokens.add(current);
        } else {
          sb.append(" ");
          sb.append(current);
          sb.append(" ");
        }
      }
    }
    System.out.println(_tokens.toString());
    s.close();
  }
}
