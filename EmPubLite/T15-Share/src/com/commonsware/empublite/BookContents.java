package com.commonsware.empublite;

import org.json.JSONArray;
import org.json.JSONObject;

public class BookContents {
  JSONObject raw=null;
  JSONArray chapters;

  BookContents(JSONObject raw) {
    this.raw=raw;
    chapters=raw.optJSONArray("chapters");
  }

  int getChapterCount() {
    return(chapters.length());
  }

  String getChapterFile(int position) {
    JSONObject chapter=chapters.optJSONObject(position);

    return(chapter.optString("file"));
  }

  String getTitle() {
    return(raw.optString("title"));
  }
}
