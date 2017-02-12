/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import java.util.List;

public class PresoContents {
  String title;
  List<Slide> slides;
  int duration;
  String baseURL;
  String baseDir;
  int id=-1;

  static class Slide {
    String image;
    String title;
  }

  @Override
  public String toString() {
    return(title);
  }

  String getSlideImage(int position) {
    return(baseDir+slides.get(position).image);
  }

  String getSlideTitle(int position) {
    return(slides.get(position).title);
  }

  String getSlideURL(int position) {
    return(baseURL+slides.get(position).image);
  }
}