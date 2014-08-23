package com.commonsware.empublite;

class NoteLoadedEvent {
  int position;
  String prose;

  NoteLoadedEvent(int position, String prose) {
    this.position=position;
    this.prose=prose;
  }

  int getPosition() {
    return(position);
  }

  String getProse() {
    return(prose);
  }
}