package edu.nyu.cs.cs2580;

import java.nio.charset.Charset;

/**
 * Created by kc on 11/26/16.
 */
public class Helper {
  public static String porterStem(String token) {
    if (token == null | token.isEmpty()) {
      return null;
    }

    Stemmer stemmer = new Stemmer();
    stemmer.add(token.toCharArray(), token.length());
    stemmer.stem();
    return stemmer.toString().toLowerCase();
  }

  public static String convertToUTF8(String s) {
    String result = null;
    try {
      result = new String(s.getBytes(Charset.defaultCharset()), "UTF-8");
    } catch(Exception e) {}
    return result;
  }
}

class Stemmer {
  private char[] b;
  private int i;
  private int i_end;
  private int j;
  private int k;
  private static final int INC = 50;

  public Stemmer() {
    b = new char[INC];
    i = 0;
    i_end = 0;
  }

  /**
   * Add a character to the word being stemmed.  When you are finished
   * adding characters, you can call stem(void) to stem the word.
   */
  public void add(char ch) {
    if (i == b.length) {
      char[] new_b = new char[i+INC];
      for (int c = 0; c < i; c++) new_b[c] = b[c];
      b = new_b;
    }
    b[i++] = ch;
  }

  /** Adds wLen characters to the word being stemmed contained in a portion
   * of a char[] array. This is like repeated calls of add(char ch), but
   * faster.
   */
  public void add(char[] w, int wLen) {
    if (i+wLen >= b.length) {
      char[] new_b = new char[i+wLen+INC];
      for (int c = 0; c < i; c++) new_b[c] = b[c];
      b = new_b;
    }
    for (int c = 0; c < wLen; c++) {
      b[i++] = w[c];
    }
  }

  /**
   * After a word has been stemmed, it can be retrieved by toString(),
   * or a reference to the internal buffer can be retrieved by getResultBuffer
   * and getResultLength (which is generally more efficient.)
   */
  public String toString() {
    return new String(b, 0, i_end);
  }

  private final boolean cons(int i) {
    switch (b[i]) {
      case 'a':
      case 'e':
      case 'i':
      case 'o':
      case 'u':
        return false;
      case 'y':
        return (i==0) ? true : !cons(i-1);
      default:
        return true;
    }
  }

  private final int m() {
    int n = 0;
    int i = 0;
    while(true) {
      if (i > j) return n;
      if (! cons(i)) break; i++;
    }
    i++;
    while(true) {
      while(true) {
        if (i > j) return n;
        if (cons(i)) break;
        i++;
      }
      i++;
      n++;
      while(true) {
        if (i > j) {
          return n;
        }
        if (! cons(i)) {
          break;
        }
        i++;
      }
      i++;
    }
  }

  private final boolean vowelinstem() {
    int i;
    for (i = 0; i <= j; i++) {
      if (! cons(i)) {
        return true;
      }
    }
    return false;
  }

  private final boolean doublec(int j) {
    if (j < 1) {
      return false;
    }

    if (b[j] != b[j-1]) {
      return false;
    }

    return cons(j);
  }

  private final boolean cvc(int i) {
    if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2)) {
      return false;
    }

    int ch = b[i];
    if (ch == 'w' || ch == 'x' || ch == 'y') {
      return false;
    }

    return true;
  }

  private final boolean ends(String s) {
    int l = s.length();
    int o = k-l+1;
    if (o < 0) {
      return false;
    }

    for (int i = 0; i < l; i++) {
      if (b[o+i] != s.charAt(i)) {
        return false;
      }
    }
    j = k-l;

    return true;
  }

  private final void setto(String s) {
    int l = s.length();
    int o = j+1;
    for (int i = 0; i < l; i++) {
      b[o+i] = s.charAt(i);
    }
    k = j+l;
  }

  private final void r(String s) {
    if (m() > 0) {
      setto(s);
    }
  }

  private final void step1()
  {  if (b[k] == 's')
  {  if (ends("sses")) k -= 2; else
  if (ends("ies")) setto("i"); else
  if (b[k-1] != 's') k--;
  }
    if (ends("eed")) { if (m() > 0) k--; } else
    if ((ends("ed") || ends("ing")) && vowelinstem())
    {  k = j;
      if (ends("at")) setto("ate"); else
      if (ends("bl")) setto("ble"); else
      if (ends("iz")) setto("ize"); else
      if (doublec(k))
      {  k--;
        {  int ch = b[k];
          if (ch == 'l' || ch == 's' || ch == 'z') k++;
        }
      }
      else if (m() == 1 && cvc(k)) setto("e");
    }
  }

  /** Stem the word placed into the Stemmer buffer through calls to add().
   * Returns true if the stemming process resulted in a word different
   * from the input.  You can retrieve the result with
   * getResultLength()/getResultBuffer() or toString().
   */
  public void stem() {
    k = i - 1;
    if (k > 1) {
      step1();
    }

    i_end = k+1; i = 0;
  }
}