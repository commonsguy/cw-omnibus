/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.appshortcuts;

import java.util.HashMap;

class Bookmark implements Comparable<Bookmark> {
  static final HashMap<String, Bookmark> MODEL=new HashMap<>();
  final String url;
  final String title;
  final String id;

  static {
    add(new Bookmark("Android Developer Home",
        "https://developer.android.com",
        "687a9ea6-f0c0-448c-9cc9-a4aa6e10a1af"));
    add(new Bookmark("Android Open Source Project",
      "https://source.android.com",
      "0ee37e25-2dac-4602-8aa2-3709ac4037c8"));
    add(new Bookmark("AOSP Source Search",
      "http://xref.opersys.com/",
      "405ba533-337e-40be-abe0-fb86cd04bf7d"));
    add(new Bookmark("Stack Overflow Android Questions",
      "https://stackoverflow.com/questions/tagged/android",
      "c9599794-cb9f-46a1-ad61-971ff2a8a172"));
    add(new Bookmark("The CommonsBlog",
      "https://commonsware.com/blog/",
      "948fe25a-44d4-49d0-a23f-2783f786040d"));
    add(new Bookmark("CWAC Community",
      "https://community.commonsware.com/c/cwac",
      "4c7fac0f-fc86-4c68-8ad8-99198fc3d433"));
  }

  private static void add(Bookmark b) {
    MODEL.put(b.id, b);
  }

  Bookmark(String title, String url, String id) {
    this.url=url;
    this.title=title;
    this.id=id;
  }

  @Override
  public int compareTo(Bookmark bookmark) {
    return(title.compareTo(bookmark.title));
  }
}
