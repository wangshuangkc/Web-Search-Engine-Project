package edu.nyu.cs.cs2580;

/**
 * Created by kc on 12/12/16.
 */
public class VideoDocumentIndexed extends Document {
  private static final long serialVersionUID = 9184892508124423115L;
  private String _speaker = "";
  private String _postTime = "";
  private String _image = "";

  public VideoDocumentIndexed(int docid) {
    super(docid);
  }

  private int _docTotalTerms = 0;

  public void setDocTotalTerms(int docTotalTerms) {
    _docTotalTerms = docTotalTerms;
  }

  public int getDocTotalTerms() {
    return _docTotalTerms;
  }

  public void setSpeaker(String speaker) {
    _speaker = speaker;
  }

  public String getSpeaker() {
    return _speaker;
  }

  public void setPostMonths(String time) {
    _postTime = time;
  }

  public String getPostMonths() {
    return _postTime;
  }

  public void setImage(String image) {
    _image = image;
  }

  public String getImage() {
    return _image;
  }
}
