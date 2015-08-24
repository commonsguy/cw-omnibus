package com.commonsware.empublite;

import java.util.List;

public class BookContents {
  List<BookContents.Chapter> chapters;

  int getChapterCount() {
    return(chapters.size());
  }

  String getChapterFile(int position) {
    return(chapters.get(position).file);
  }

  static class Chapter {
    String file;
  }
}