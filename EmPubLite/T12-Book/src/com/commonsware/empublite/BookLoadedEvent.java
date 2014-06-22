package com.commonsware.empublite;

public class BookLoadedEvent {
  private BookContents contents=null;
  
  public BookLoadedEvent(BookContents contents) {
    this.contents=contents;
  }
  
  public BookContents getBook() {
    return(contents);
  }
}
