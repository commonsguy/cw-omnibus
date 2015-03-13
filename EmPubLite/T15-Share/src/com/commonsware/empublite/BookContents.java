package com.commonsware.empublite;

import java.util.List;

public class BookContents {
  String title;
  List<BookContents.Chapter> chapters;
  
  int getChapterCount() {
    return(chapters.size());
  }

  String getChapterFile(int position) {
    return(chapters.get(position).file);
  }

  String getTitle() {
    return(title);
  }
  
  static class Chapter {
    String file;
    String title;
  }
}
